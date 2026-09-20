import { Capacitor } from '@capacitor/core';
import { CapacitorSQLite, SQLiteConnection, type SQLiteDBConnection } from '@capacitor-community/sqlite';
import type { Event } from './models';

const storageKey = 'dia-mundial-custom-events';
let connection: SQLiteDBConnection | undefined;

async function sqlite(): Promise<SQLiteDBConnection> {
  if (!connection) {
    const sqliteConnection = new SQLiteConnection(CapacitorSQLite);
    connection = await sqliteConnection.createConnection('dia_mundial', false, 'no-encryption', 1, false);
    await connection.open();
    await connection.execute(`CREATE TABLE IF NOT EXISTS custom_events (
      date TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, country TEXT NOT NULL, category TEXT NOT NULL DEFAULT 'culture', image TEXT, url TEXT
    );`);
    const columns = await connection.query('PRAGMA table_info(custom_events)');
    const hasCategory = (columns.values ?? []).some((column) => column.name === 'category');
    if (!hasCategory) {
      await connection.execute("ALTER TABLE custom_events ADD COLUMN category TEXT NOT NULL DEFAULT 'culture'");
    }
    const hasImage = (columns.values ?? []).some((column) => column.name === 'image');
    if (!hasImage) await connection.execute('ALTER TABLE custom_events ADD COLUMN image TEXT');
    const hasUrl = (columns.values ?? []).some((column) => column.name === 'url');
    if (!hasUrl) await connection.execute('ALTER TABLE custom_events ADD COLUMN url TEXT');
  }
  return connection;
}

function browserEvents(): Event[] {
  const raw = localStorage.getItem(storageKey);
  if (!raw) return [];
  try { return JSON.parse(raw) as Event[]; } catch (error) {
    console.error('No se pudieron leer las celebraciones locales', error);
    return [];
  }
}

export async function loadCustomEvents(): Promise<Event[]> {
  if (!Capacitor.isNativePlatform()) return browserEvents();
  const db = await sqlite();
  const result = await db.query('SELECT date, title, description, country, category, image, url FROM custom_events ORDER BY date');
  return (result.values ?? []).map((event) => ({ ...event, custom: true })) as Event[];
}

export async function saveCustomEvent(event: Event): Promise<void> {
  if (!event.title.trim() || !/^\d{2}-\d{2}$/.test(event.date)) throw new Error('La fecha y el título son obligatorios');
  if (!Capacitor.isNativePlatform()) {
    const events = browserEvents().filter((item) => item.date !== event.date);
    localStorage.setItem(storageKey, JSON.stringify([...events, { ...event, custom: true }]));
    return;
  }
  const db = await sqlite();
  await db.run('INSERT OR REPLACE INTO custom_events(date, title, description, country, category, image, url) VALUES (?, ?, ?, ?, ?, ?, ?)',
    [event.date, event.title.trim(), event.description.trim(), event.country.trim() || 'Global', event.category ?? 'culture', event.image?.trim() || null, event.url?.trim() || null]);
}

export async function deleteCustomEvent(date: string): Promise<void> {
  if (!Capacitor.isNativePlatform()) {
    localStorage.setItem(storageKey, JSON.stringify(browserEvents().filter((event) => event.date !== date)));
    return;
  }
  const db = await sqlite();
  await db.run('DELETE FROM custom_events WHERE date = ?', [date]);
}
