import { useState } from 'react';
import { IonButton } from '@ionic/react';
import type { Event, Language } from '../../models';
import './Calendar.css';
import { resolveLanguage } from '../../i18n';

const currentDay = () => {
  const now = new Date();
  return `${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
};

const monthName = (date: Date, language: Language) => new Intl.DateTimeFormat(language, { month: 'long', year: 'numeric' }).format(date);

interface CalendarProps {
  events: Event[];
  selected: string;
  onSelect: (date: string) => void;
  onLongPress: (date: string) => void;
  onToday: () => void;
  language: Language;
}

export function Calendar({ events, selected, onSelect, onLongPress, onToday, language }: CalendarProps) {
  const [month, setMonth] = useState(() => {
    const date = new Date();
    return new Date(date.getFullYear(), date.getMonth(), 1);
  });
  const goToToday = () => {
    const currentDate = new Date();
    setMonth(new Date(currentDate.getFullYear(), currentDate.getMonth(), 1));
    onToday();
  };
  const days = new Date(month.getFullYear(), month.getMonth() + 1, 0).getDate();
  const offset = (new Date(month.getFullYear(), month.getMonth(), 1).getDay() + 6) % 7;
  const dates = Array.from({ length: offset + days }, (_, index) => index < offset ? '' : `${String(month.getMonth() + 1).padStart(2, '0')}-${String(index - offset + 1).padStart(2, '0')}`);
  const hasEvent = new Set(events.map((event) => event.date));

  return <section className="calendar">
    <div className="calendar-heading">
      <IonButton fill="clear" onClick={() => setMonth(new Date(month.getFullYear(), month.getMonth() - 1, 1))}>‹</IonButton>
      <strong>{monthName(month, language)}</strong>
      <IonButton fill="clear" onClick={() => setMonth(new Date(month.getFullYear(), month.getMonth() + 1, 1))}>›</IonButton>
      <IonButton fill="outline" size="small" onClick={goToToday}>{({ es: 'Hoy', en: 'Today', fr: "Aujourd'hui", pt: 'Hoje', ca: 'Avui', zh: '今天', ja: '今日' } as Record<string, string>)[resolveLanguage(language)]}</IonButton>
    </div>
    <div className="weekdays">{['L', 'M', 'X', 'J', 'V', 'S', 'D'].map((day) => <b key={day}>{day}</b>)}</div>
    <div className="calendar-grid">{dates.map((date, index) => {
      let longPressTimer: number | undefined;
      let longPressTriggered = false;
      const startLongPress = () => {
        if (!date) return;
        longPressTriggered = false;
        longPressTimer = window.setTimeout(() => {
          longPressTriggered = true;
          onLongPress(date);
        }, 550);
      };
      const cancelLongPress = () => {
        if (longPressTimer !== undefined) window.clearTimeout(longPressTimer);
      };
      return <button
        className={`${date === selected ? 'selected ' : ''}${date === currentDay() ? 'today' : ''}`}
        key={`${date}-${index}`}
        disabled={!date}
        onPointerDown={startLongPress}
        onPointerUp={cancelLongPress}
        onPointerLeave={cancelLongPress}
        onPointerCancel={cancelLongPress}
        onClick={() => date && !longPressTriggered && onSelect(date)}
      >{date && Number(date.slice(3))}<span className={hasEvent.has(date) ? 'dot' : ''} /></button>;
    })}</div>
  </section>;
}
