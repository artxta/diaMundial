import { IonButton, IonButtons, IonContent, IonHeader, IonIcon, IonItem, IonLabel, IonList, IonModal, IonTitle, IonToolbar } from '@ionic/react';
import { closeOutline, copyOutline, logoWhatsapp, paperPlaneOutline, shareSocialOutline } from 'ionicons/icons';
import { useState } from 'react';
import type { Event, Language } from '../../models';
import { t } from '../../i18n';
import './ShareButton.css';

interface ShareButtonProps {
  event: Event;
  language: Language;
  fill?: 'clear' | 'outline' | 'solid' | 'default';
}

function shareText(event: Event): string {
  return `${event.title}\n${event.date}\n${event.description}`;
}

export function ShareButton({ event, language, fill = 'clear' }: ShareButtonProps) {
  const [isOpen, setIsOpen] = useState(false);
  const text = shareText(event);

  const shareAsWhatsAppStatus = async () => {
    // Android opens the native share sheet; after choosing WhatsApp, the user can select "Mi estado".
    if (navigator.share) {
      await navigator.share({ title: event.title, text });
      return;
    }
    openTarget(`whatsapp://send?text=${encodeURIComponent(text)}`, `https://wa.me/?text=${encodeURIComponent(text)}`);
  };
  const copyShareText = async () => {
    if (!navigator.clipboard) throw new Error('El portapapeles no está disponible');
    await navigator.clipboard.writeText(text);
  };

  const openTarget = (url: string, fallbackUrl: string) => {
    const opened = window.open(url, '_blank');
    if (!opened) window.location.href = fallbackUrl;
  };

  return <>
    <IonButton fill={fill} onClick={(event) => { event.stopPropagation(); setIsOpen(true); }} aria-label={t(language, 'share')}>
      <IonIcon icon={shareSocialOutline} slot={fill === 'clear' ? undefined : 'start'} />
      {fill !== 'clear' && t(language, 'share')}
    </IonButton>
    <IonModal
      className="share-modal"
      isOpen={isOpen}
      onDidDismiss={() => setIsOpen(false)}
      onClick={(event) => event.stopPropagation()}
    >
      <IonHeader>
        <IonToolbar>
          <IonTitle>{t(language, 'share')}</IonTitle>
          <IonButtons slot="end">
            <IonButton onClick={() => setIsOpen(false)} aria-label={t(language, 'close')}>
              <IonIcon icon={closeOutline} />
            </IonButton>
          </IonButtons>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <IonList className="share-options">
          <IonItem button detail onClick={() => { setIsOpen(false); void shareAsWhatsAppStatus(); }}>
            <IonIcon slot="start" icon={logoWhatsapp} />
            <IonLabel>{t(language, 'shareWhatsApp')}</IonLabel>
          </IonItem>
          <IonItem button detail onClick={() => { setIsOpen(false); openTarget(`tg://msg?text=${encodeURIComponent(text)}`, `https://t.me/share/url?text=${encodeURIComponent(text)}`); }}>
            <IonIcon slot="start" icon={paperPlaneOutline} />
            <IonLabel>{t(language, 'shareTelegram')}</IonLabel>
          </IonItem>
          <IonItem button detail onClick={() => {
            setIsOpen(false);
            void copyShareText().catch((error: unknown) => console.error('No se pudo copiar el contenido para compartir', error));
          }}>
            <IonIcon slot="start" icon={copyOutline} />
            <IonLabel>{t(language, 'copy')}</IonLabel>
          </IonItem>
        </IonList>
      </IonContent>
    </IonModal>
  </>;
}
