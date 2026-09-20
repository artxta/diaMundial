import { IonButton, IonCard, IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle, IonIcon } from '@ionic/react';
import { createOutline, heart, heartOutline, trashOutline } from 'ionicons/icons';
import { classify, type Event, type Language } from '../../models';
import { categoryName } from '../../i18n';
import { CategoryIcon } from '../CategoryIcon';
import './EventCard.css';
import { ShareButton } from '../ShareButton';

interface EventCardProps {
  event: Event;
  favorite: boolean;
  onFavorite: () => void;
  onEdit: () => void;
  onDelete: () => void;
  language: Language;
  shareFill?: 'clear' | 'solid';
}

export function EventCard({ event, favorite, onFavorite, onEdit, onDelete, language, shareFill = 'clear' }: EventCardProps) {
  return <IonCard className="event-card">
    <IonCardHeader>
      <IonCardSubtitle><span className="category-with-icon"><CategoryIcon category={classify(event)} /> {event.date} · {event.country} · {categoryName(language, classify(event))}</span></IonCardSubtitle>
      <IonCardTitle>{event.title}</IonCardTitle>
    </IonCardHeader>
    <IonCardContent>
      <p>{event.description}</p>
      <div className="card-actions">
        <IonButton fill="clear" onClick={onFavorite}><IonIcon icon={favorite ? heart : heartOutline} color={favorite ? 'danger' : undefined} /></IonButton>
        <ShareButton event={event} language={language} fill={shareFill} />
        {event.custom && <><IonButton fill="clear" onClick={onEdit}><IonIcon icon={createOutline} /></IonButton><IonButton fill="clear" color="danger" onClick={onDelete}><IonIcon icon={trashOutline} /></IonButton></>}
      </div>
    </IonCardContent>
  </IonCard>;
}
