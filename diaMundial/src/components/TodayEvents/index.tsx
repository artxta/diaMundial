import { IonItem, IonLabel, IonList, IonText } from '@ionic/react';
import { classify, type Event } from '../../models';
import type { Language } from '../../models';
import { categoryName, t } from '../../i18n';
import { CategoryIcon } from '../CategoryIcon';
import './TodayEvents.css';

interface TodayEventsProps {
  events: Event[];
  language: Language;
}

export function TodayEvents({ events, language }: TodayEventsProps) {
  return <section className="today-events" aria-labelledby="today-events-title">
    <IonText><h2 id="today-events-title">{t(language, 'todayCelebrations')}</h2></IonText>
    {events.length > 0 ? <IonList inset>
      {events.map((event) => <IonItem key={`${event.date}-${event.title}`}>
        <IonLabel>
          <h3>{event.title}</h3>
          <p className="category-with-icon"><CategoryIcon category={classify(event)} /> {categoryName(language, classify(event))} · {event.country}</p>
        </IonLabel>
      </IonItem>)}
    </IonList> : <IonText color="medium"><p className="today-empty">{t(language, 'noCelebrationsToday')}</p></IonText>}
  </section>;
}
