import { IonButton, IonButtons, IonIcon, IonModal, IonText, IonTitle, IonToolbar } from '@ionic/react';
import { close } from 'ionicons/icons';
import { useEffect, useState } from 'react';
import type { Language } from '../../models';
import { t } from '../../i18n';
import './AppTour.css';

interface AppTourProps {
  language: Language;
  isOpen: boolean;
  onFinish: () => void;
}

interface TourStep {
  target: string;
  titleKey: Parameters<typeof t>[1];
  descriptionKey: Parameters<typeof t>[1];
}

const steps: TourStep[] = [
  { target: '[data-tour="today"]', titleKey: 'tourTodayTitle', descriptionKey: 'tourTodayDescription' },
  { target: '[data-tour="search"]', titleKey: 'tourSearchTitle', descriptionKey: 'tourSearchDescription' },
  { target: '[data-tour="filters"]', titleKey: 'tourFiltersTitle', descriptionKey: 'tourFiltersDescription' },
  { target: '[data-tour="calendar"]', titleKey: 'tourCalendarTitle', descriptionKey: 'tourCalendarDescription' },
  { target: '[data-tour="results"]', titleKey: 'tourResultsTitle', descriptionKey: 'tourResultsDescription' },
  { target: '[data-tour="notifications"]', titleKey: 'tourNotificationsTitle', descriptionKey: 'tourNotificationsDescription' },
  { target: '[data-tour="navigation"]', titleKey: 'tourNavigationTitle', descriptionKey: 'tourNavigationDescription' }
];

export function AppTour({ language, isOpen, onFinish }: AppTourProps) {
  const [stepIndex, setStepIndex] = useState(0);
  const step = steps[stepIndex];

  useEffect(() => {
    if (!isOpen) return;
    const target = document.querySelector(step.target);
    target?.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }, [isOpen, step]);

  const finish = () => {
    localStorage.setItem('dia-mundial-tour-completed', 'true');
    onFinish();
  };

  return <IonModal className="app-tour-modal" isOpen={isOpen} onDidDismiss={finish}>
    <IonToolbar>
      <IonTitle>{t(language, 'tourTitle')}</IonTitle>
      <IonButtons slot="end"><IonButton onClick={finish} aria-label={t(language, 'close')}><IonIcon icon={close} /></IonButton></IonButtons>
    </IonToolbar>
    <div className="app-tour-content">
      <IonText><h2>{t(language, step.titleKey)}</h2><p>{t(language, step.descriptionKey)}</p></IonText>
      <div className="app-tour-progress" aria-label={`${stepIndex + 1} / ${steps.length}`}>
        {steps.map((item, index) => <span className={index === stepIndex ? 'active' : ''} key={item.target} />)}
      </div>
      <div className="app-tour-actions">
        <IonButton fill="clear" onClick={finish}>{t(language, 'tourSkip')}</IonButton>
        <IonButton onClick={() => stepIndex === steps.length - 1 ? finish() : setStepIndex((index) => index + 1)}>
          {t(language, stepIndex === steps.length - 1 ? 'tourFinish' : 'tourNext')}
        </IonButton>
      </div>
    </div>
  </IonModal>;
}
