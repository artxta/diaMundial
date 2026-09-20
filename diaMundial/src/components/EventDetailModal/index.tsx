import { IonButton, IonButtons, IonContent, IonHeader, IonIcon, IonModal, IonTitle, IonToolbar } from '@ionic/react';
import { close, createOutline, heart, heartOutline } from 'ionicons/icons';
import { classify, type Event } from '../../models';
import type { Language } from '../../models';
import { categoryName, t } from '../../i18n';
import { CategoryIcon } from '../CategoryIcon';
import './EventDetailModal.css';
import { ShareButton } from '../ShareButton';

interface EventDetailModalProps {
  date: string;
  events: Event[];
  favorites: string[];
  isOpen: boolean;
  onClose: () => void;
  onCreate: () => void;
  onEdit: (event: Event) => void;
  onFavorite: (event: Event) => void;
  language: Language;
}

export function EventDetailModal({ date, events, favorites, isOpen, onClose, onCreate, onEdit, onFavorite, language }: EventDetailModalProps) {
  return <IonModal className="event-detail-modal" isOpen={isOpen} onDidDismiss={onClose}>
    <IonHeader>
      <IonToolbar>
        <IonTitle>{events.length > 1 ? `${events.length} ${t(language, 'celebrations')}` : events[0]?.title ?? t(language, 'selectedDay')}</IonTitle>
        <IonButtons slot="end"><IonButton onClick={onClose} aria-label={t(language, 'close')}><IonIcon icon={close} /></IonButton></IonButtons>
      </IonToolbar>
    </IonHeader>
    <IonContent className="ion-padding">
      {events.length === 0 && <><h2>{t(language, 'noCelebrations')}</h2><p><strong>{t(language, 'date')}:</strong> {date}</p><p>{t(language, 'noCelebrationsForDay')}</p><IonButton onClick={onCreate}>{t(language, 'createCustomDay')}</IonButton></>}
      {events.map((event) => {
        const favorite = favorites.includes(event.date);
        return <article key={`${event.date}-${event.title}`}>
          <h2>{event.title}</h2>
          <p><strong>{t(language, 'date')}:</strong> {event.date}</p>
          <p className="category-with-icon"><strong>{t(language, 'category')}:</strong> <CategoryIcon category={classify(event)} /> {categoryName(language, classify(event))}</p>
          <p>{event.description}</p>
          <div className="card-actions">
            <IonButton onClick={() => onEdit(event)} disabled={!event.custom}><IonIcon slot="start" icon={createOutline} />{t(language, 'edit')}</IonButton>
            <ShareButton event={event} language={language} fill="solid" />
            <IonButton onClick={() => onFavorite(event)} color={favorite ? 'danger' : 'primary'}><IonIcon slot="start" icon={favorite ? heart : heartOutline} />{favorite ? t(language, 'removeFavorite') : t(language, 'addFavorite')}</IonButton>
          </div>
        </article>;
      })}
      <IonButton fill="outline" onClick={onClose}>{t(language, 'close')}</IonButton>
    </IonContent>
  </IonModal>;
}
