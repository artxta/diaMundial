import { useEffect, useMemo, useState } from 'react';
import {
  IonButton, IonButtons, IonContent, IonFooter, IonHeader, IonIcon, IonItem, IonLabel,
  IonList, IonModal, IonPage, IonTabBar,
  IonTabButton, IonText, IonTitle, IonToolbar
} from '@ionic/react';
import { App as CapacitorApp } from '@capacitor/app';
import { StatusBar, Style } from '@capacitor/status-bar';
import { add, calendarOutline, close, heartOutline, notificationsOutline, optionsOutline } from 'ionicons/icons';
import { deleteCustomEvent, loadCustomEvents, saveCustomEvent } from './database';
import { classify, type AppSettings, type Event, type Language } from './models';
import { Calendar } from './components/Calendar';
import { DaySearch } from './components/DaySearch';
import { EventCard } from './components/EventCard';
import { EventDetailModal } from './components/EventDetailModal';
import { EventForm } from './components/EventForm';
import { Filters } from './components/Filters';
import { Settings } from './components/Settings';
import { NotificationSettings } from './components/NotificationSettings';
import { AppTour } from './components/AppTour';
import { TodayEvents } from './components/TodayEvents';
import { resolveLanguage, t } from './i18n';

const today = () => {
  const now = new Date();
  return `${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
};
const monthName = (date: Date, language: Language) => new Intl.DateTimeFormat(language, { month: 'long', year: 'numeric' }).format(date);
const defaultSettings: AppSettings = { language: 'system', theme: 'system', fontScale: 1, fontFamily: 'system', notificationMode: 'none', notificationTime: '09:00', notificationDays: [1, 2, 3, 4, 5], category: 'all', favorites: [] };

function useEvents(language: Language) {
  const [events, setEvents] = useState<Event[]>([]);
  useEffect(() => {
    let active = true;
    const resolvedLanguage = resolveLanguage(language);
    fetch(`/data/${resolvedLanguage}.json`).then((response) => response.ok ? response.json() : fetch('/data/en.json').then((fallback) => fallback.json())).then((data: Event[]) => {
      if (active) setEvents(data);
    }).catch((error: unknown) => console.error('No se pudieron cargar los días', error));
    return () => { active = false; };
  }, [language]);
  return [events, setEvents] as const;
}

export function App() {
  const [settings, setSettings] = useState<AppSettings>(() => {
    try { return { ...defaultSettings, ...JSON.parse(localStorage.getItem('dia-mundial-settings') ?? '{}') } as AppSettings; }
    catch (error: unknown) { console.error('No se pudieron leer las preferencias', error); return defaultSettings; }
  });
  const [events, setEvents] = useEvents(settings.language);
  const [custom, setCustom] = useState<Event[]>([]);
  const [selected, setSelected] = useState(today());
  const [search, setSearch] = useState('');
  const [country, setCountry] = useState('all');
  const [tab, setTab] = useState<'home' | 'favorites' | 'settings'>('home');
  const [form, setForm] = useState<Event | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [detailDate, setDetailDate] = useState(selected);
  const [detailEvents, setDetailEvents] = useState<Event[]>([]);
  const [detailOpen, setDetailOpen] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [tourOpen, setTourOpen] = useState(() => localStorage.getItem('dia-mundial-tour-completed') !== 'true');

  useEffect(() => { loadCustomEvents().then(setCustom).catch((error: unknown) => console.error('No se pudieron cargar días personalizados', error)); }, []);
  useEffect(() => {
    const fontStacks = {
      system: 'Roboto, "Google Sans", system-ui, -apple-system, sans-serif',
      roboto: 'Roboto, "Arial", sans-serif',
      rounded: '"Nunito", "Arial Rounded MT Bold", sans-serif',
      serif: 'Georgia, "Times New Roman", serif',
      mono: '"Roboto Mono", "Courier New", monospace',
      cursive: '"Brush Script MT", "Segoe Script", "Apple Chancery", cursive',
      handwriting: '"Comic Sans MS", "Bradley Hand", "Segoe Print", cursive'
    } as const;
    localStorage.setItem('dia-mundial-settings', JSON.stringify(settings));
    document.documentElement.dataset.theme = settings.theme;
    document.documentElement.style.setProperty('--app-font-scale', String(settings.fontScale));
    const fontFamily = fontStacks[settings.fontFamily] ?? fontStacks.system;
    document.documentElement.style.setProperty('--app-font-family', fontFamily);
    document.documentElement.style.setProperty('--ion-font-family', fontFamily);
  }, [settings]);
  useEffect(() => {
    const updateSystemUi = async () => {
      const dark = settings.theme === 'dark' || (settings.theme === 'system' && window.matchMedia('(prefers-color-scheme: dark)').matches);
      document.documentElement.dataset.theme = dark ? 'dark' : 'light';
      try {
        await StatusBar.setOverlaysWebView({ overlay: false });
        await StatusBar.setStyle({ style: dark ? Style.Light : Style.Dark });
        await StatusBar.setBackgroundColor({ color: dark ? '#17171c' : '#fffbff' });
      } catch (error: unknown) {
        console.warn('No se pudo configurar la barra de estado', error);
      }
    };
    void updateSystemUi();
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handleSystemTheme = () => {
      if (settings.theme === 'system') void updateSystemUi();
    };
    mediaQuery.addEventListener('change', handleSystemTheme);
    return () => mediaQuery.removeEventListener('change', handleSystemTheme);
  }, [settings.theme]);
  useEffect(() => {
    const listener = CapacitorApp.addListener('backButton', ({ canGoBack }) => {
      if (showForm) { setShowForm(false); setForm(null); return; }
      if (detailOpen) { setDetailOpen(false); return; }
      if (notificationsOpen) { setNotificationsOpen(false); return; }
      if (canGoBack) window.history.back();
    });
    return () => { void listener.then((handle) => handle.remove()); };
  }, [detailOpen, notificationsOpen, showForm]);
  const allEvents = useMemo(() => [...events, ...custom], [events, custom]);
  const countries = useMemo(() => [...new Set(allEvents.map((event) => event.country))].sort(), [allEvents]);
  const filteredEvents = useMemo(() => allEvents.filter((event) =>
    (country === 'all' || event.country === country) &&
    (settings.category === 'all' || classify(event) === settings.category)
  ), [allEvents, country, settings.category]);
  const visible = useMemo(() => allEvents.filter((event) => {
    const matchesTab = tab !== 'favorites' || settings.favorites.includes(event.date);
    const searchText = search.trim().toLocaleLowerCase();
    const searchableText = `${event.title} ${event.description}`.toLocaleLowerCase();
    const matchesSearch = !searchText || searchableText.includes(searchText);
    return matchesTab && matchesSearch && filteredEvents.includes(event);
  }).sort((a, b) => a.date.localeCompare(b.date)), [allEvents, filteredEvents, search, settings.favorites, tab]);
  const selectedEvents = visible.filter((event) => event.date === selected);
  const displayedEvents = tab === 'home' && !search.trim() ? selectedEvents : visible;
  const searchDays = (query: string) => {
    setSearch(query.trim());
    requestAnimationFrame(() => document.querySelector('.event-list')?.scrollIntoView({ behavior: 'smooth', block: 'start' }));
  };
  const update = (patch: Partial<AppSettings>) => setSettings((current) => ({ ...current, ...patch }));
  const toggleFavorite = (date: string) => update({ favorites: settings.favorites.includes(date) ? settings.favorites.filter((item) => item !== date) : [...settings.favorites, date] });
  const save = async (event: Event) => { await saveCustomEvent(event); setCustom(await loadCustomEvents()); setShowForm(false); setForm(null); setSelected(event.date); };
  const remove = async (event: Event) => { await deleteCustomEvent(event.date); setCustom(await loadCustomEvents()); };
  const selectDate = (date: string) => {
    setSelected(date);
    setDetailDate(date);
    setDetailEvents(allEvents.filter((event) => event.date === date));
    setDetailOpen(true);
  };
  const goToToday = () => {
    setSelected(today());
  };
  const createEvent = () => {
    setForm({ date: detailDate, title: '', description: '', country: 'España', custom: true });
    setDetailEvents([]);
    setDetailOpen(false);
    setShowForm(true);
  };
  const createEventForDate = (date: string) => {
    setSelected(date);
    setDetailDate(date);
    setForm({ date, title: '', description: '', country: 'España', custom: true });
    setDetailOpen(false);
    setShowForm(true);
  };
  const editEvent = (event: Event) => {
    setForm(event);
    setDetailEvents([]);
    setDetailOpen(false);
    setShowForm(true);
  };
  const title = tab === 'favorites' ? t(settings.language, 'favorites') : tab === 'settings' ? t(settings.language, 'settings') : t(settings.language, 'appTitle');
  return <>
    <IonPage><IonHeader><IonToolbar><IonTitle>{title}</IonTitle>{tab === 'home' && <IonButtons slot="end"><span data-tour="notifications"><IonButton onClick={() => setNotificationsOpen(true)} aria-label={t(settings.language, 'notifications')}><IonIcon icon={notificationsOutline} /></IonButton></span><IonButton onClick={() => setShowForm(true)} aria-label={t(settings.language, 'add')}><IonIcon icon={add} /></IonButton></IonButtons>}</IonToolbar></IonHeader>
      <IonContent fullscreen>
        {tab === 'settings' ? <Settings settings={settings} update={update} onOpenNotifications={() => setNotificationsOpen(true)} /> : <>{tab === 'home' && <><div data-tour="today"><TodayEvents events={filteredEvents.filter((event) => event.date === today())} language={settings.language} /></div><div data-tour="search"><DaySearch value={search} language={settings.language} onSearch={searchDays} /></div><div data-tour="filters"><Filters country={country} countries={countries} category={settings.category} language={settings.language} onCountryChange={setCountry} onCategoryChange={(category) => update({ category })} /></div><div data-tour="calendar"><Calendar events={filteredEvents} selected={selected} onSelect={selectDate} onLongPress={createEventForDate} onToday={goToToday} language={settings.language} /></div></>}{tab === 'favorites' && <IonText className="page-hint"><p>{t(settings.language, 'favorites')}</p></IonText>}<div className="event-list" data-tour="results">{displayedEvents.map((event) =>         <EventCard key={`${event.date}-${event.title}`} event={event} language={settings.language} favorite={settings.favorites.includes(event.date)} onFavorite={() => toggleFavorite(event.date)} onEdit={() => editEvent(event)} onDelete={() => remove(event)} shareFill={tab === 'favorites' ? 'solid' : 'clear'} />)}{displayedEvents.length === 0 && <IonText className="empty"><p>{t(settings.language, 'noFilterResults')}</p></IonText>}</div></>}
      </IonContent>
      <IonFooter>
        <IonTabBar selectedTab={tab}>
          <IonTabButton tab="home" onClick={() => setTab('home')} data-tour="navigation"><IonIcon icon={calendarOutline} /><IonLabel>{t(settings.language, 'home')}</IonLabel></IonTabButton>
          <IonTabButton tab="favorites" onClick={() => setTab('favorites')}><IonIcon icon={heartOutline} /><IonLabel>{t(settings.language, 'favorites')}</IonLabel></IonTabButton>
          <IonTabButton tab="settings" onClick={() => setTab('settings')}><IonIcon icon={optionsOutline} /><IonLabel>{t(settings.language, 'settings')}</IonLabel></IonTabButton>
        </IonTabBar>
      </IonFooter>
    </IonPage>
    <EventDetailModal
      date={detailDate}
      events={detailEvents}
      favorites={settings.favorites}
      isOpen={detailOpen}
      onClose={() => setDetailOpen(false)}
      onCreate={createEvent}
      onEdit={editEvent}
      onFavorite={(event) => toggleFavorite(event.date)}
      language={settings.language}
    />
    <NotificationSettings settings={settings} update={update} isOpen={notificationsOpen} onClose={() => setNotificationsOpen(false)} />
    <AppTour language={settings.language} isOpen={tourOpen} onFinish={() => setTourOpen(false)} />
    <IonModal isOpen={showForm} onDidDismiss={() => { setShowForm(false); setForm(null); }}>
      <IonHeader>
        <IonToolbar>
          <IonTitle>{form ? t(settings.language, 'editCelebration') : t(settings.language, 'newCelebration')}</IonTitle>
              <IonButtons slot="end"><IonButton onClick={() => { setShowForm(false); setForm(null); }} aria-label={t(settings.language, 'close')}><IonIcon icon={close} /></IonButton></IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent><EventForm initial={form ?? undefined} language={settings.language} onSave={save} onCancel={() => { setShowForm(false); setForm(null); }} /></IonContent>
    </IonModal>
  </>;
}
