import {
  IonButton, IonButtons, IonCheckbox, IonContent, IonHeader, IonItem, IonLabel,
  IonIcon, IonInput, IonList, IonModal, IonSelect, IonSelectOption, IonTitle, IonToolbar
} from '@ionic/react';
import { keypadOutline } from 'ionicons/icons';
import { useEffect, useRef, useState, type PointerEvent } from 'react';
import type { AppSettings, NotificationDay, NotificationMode } from '../../models';
import { t } from '../../i18n';
import './NotificationSettings.css';

interface NotificationSettingsProps {
  settings: AppSettings;
  update: (patch: Partial<AppSettings>) => void;
  isOpen: boolean;
  onClose: () => void;
}

const dayKeys = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'] as const;
const dayNumbers: NotificationDay[] = [1, 2, 3, 4, 5, 6, 0];

export function NotificationSettings({ settings, update, isOpen, onClose }: NotificationSettingsProps) {
  const [timeOpen, setTimeOpen] = useState(false);
  const [clockPart, setClockPart] = useState<'hour' | 'minute'>('hour');
  const [draftTime, setDraftTime] = useState(settings.notificationTime);
  const [hourText, setHourText] = useState('');
  const [minuteText, setMinuteText] = useState('');
  const [keyboardMode, setKeyboardMode] = useState(false);
  const draggingRef = useRef(false);
  const hourInputRef = useRef<HTMLIonInputElement>(null);
  const minuteInputRef = useRef<HTMLIonInputElement>(null);
  const [draftHour, draftMinute] = draftTime.split(':').map(Number);
  const hour = Number.isFinite(draftHour) ? draftHour : 9;
  const minute = Number.isFinite(draftMinute) ? draftMinute : 0;
  const clockValues = clockPart === 'hour'
    ? Array.from({ length: 24 }, (_, index) => index)
    : Array.from({ length: 12 }, (_, index) => index * 5);
  const openTimePicker = () => {
    setDraftTime(settings.notificationTime);
    const [selectedHour, selectedMinute] = settings.notificationTime.split(':');
    setHourText(selectedHour);
    setMinuteText(selectedMinute);
    setClockPart('hour');
    setTimeOpen(true);
  };
  const syncDraftTime = (value: string) => {
    setDraftTime(value);
    const [nextHour, nextMinute] = value.split(':');
    setHourText(nextHour);
    setMinuteText(nextMinute);
  };
  const selectClockValue = (value: number) => {
    if (clockPart === 'hour') {
      syncDraftTime(`${String(value).padStart(2, '0')}:${String(minute).padStart(2, '0')}`);
      setClockPart('minute');
    } else {
      syncDraftTime(`${String(hour).padStart(2, '0')}:${String(value).padStart(2, '0')}`);
    }
  };
  const saveTime = () => {
    const match = /^([01]\d|2[0-3]):([0-5]\d)$/.exec(draftTime);
    if (match) update({ notificationTime: draftTime });
    setTimeOpen(false);
  };
  useEffect(() => {
    if (keyboardMode) {
      requestAnimationFrame(() => { void hourInputRef.current?.setFocus(); });
    }
  }, [keyboardMode]);
  const updateClockFromPointer = (event: PointerEvent<HTMLDivElement>) => {
    event.preventDefault();
    const bounds = event.currentTarget.getBoundingClientRect();
    const x = event.clientX - (bounds.left + bounds.width / 2);
    const y = event.clientY - (bounds.top + bounds.height / 2);
    const distance = Math.sqrt(x * x + y * y);
    let angle = Math.atan2(x, -y) * 180 / Math.PI;
    if (angle < 0) angle += 360;
    if (clockPart === 'hour') {
      const hourValue = Math.round(angle / 30) % 12;
      const nextHour = hourValue + (distance < 68 ? 12 : 0);
      syncDraftTime(`${String(nextHour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`);
    } else {
      const nextMinute = (Math.round(angle / 6) * 1) % 60;
      syncDraftTime(`${String(hour).padStart(2, '0')}:${String(nextMinute).padStart(2, '0')}`);
    }
  };
  const toggleDay = (day: NotificationDay, checked: boolean) => {
    const days = checked
      ? [...new Set([...settings.notificationDays, day])] as NotificationDay[]
      : settings.notificationDays.filter((item) => item !== day);
    update({ notificationDays: days });
  };

  return <IonModal className="notification-modal" isOpen={isOpen} onDidDismiss={onClose}>
    <IonHeader>
      <IonToolbar>
        <IonTitle>{t(settings.language, 'notifications')}</IonTitle>
        <IonButtons slot="end"><IonButton onClick={onClose}>{t(settings.language, 'close')}</IonButton></IonButtons>
      </IonToolbar>
    </IonHeader>
    <IonContent>
      <IonList className="notification-settings">
        <IonItem>
          <IonLabel>{t(settings.language, 'notificationMode')}</IonLabel>
          <IonSelect interface="modal" interfaceOptions={{ header: `🔔 ${t(settings.language, 'notificationMode')}` }} value={settings.notificationMode} onIonChange={(event) => update({ notificationMode: event.detail.value as NotificationMode })}>
            <IonSelectOption value="none">🔕 {t(settings.language, 'noNotification')}</IonSelectOption>
            <IonSelectOption value="silent">🔇 {t(settings.language, 'silentNotification')}</IonSelectOption>
            <IonSelectOption value="sound">🔔 {t(settings.language, 'soundNotification')}</IonSelectOption>
          </IonSelect>
        </IonItem>
        <IonItem>
          <IonLabel>{t(settings.language, 'notificationTime')}</IonLabel>
          <IonButton className="notification-time" fill="outline" disabled={settings.notificationMode === 'none'} onClick={openTimePicker}>
            {settings.notificationTime}
          </IonButton>
        </IonItem>
        <IonItem lines="none"><IonLabel><strong>{t(settings.language, 'notificationDays')}</strong></IonLabel></IonItem>
        {dayKeys.map((key, index) => {
          const day = dayNumbers[index];
          return <IonItem key={key}>
            <IonLabel>{t(settings.language, key)}</IonLabel>
            <IonCheckbox slot="end" checked={settings.notificationDays.includes(day)} disabled={settings.notificationMode === 'none'} onIonChange={(event) => toggleDay(day, event.detail.checked)} />
          </IonItem>;
        })}
      </IonList>
      <IonModal className="clock-modal" isOpen={timeOpen} onDidDismiss={() => setTimeOpen(false)}>
        <IonHeader>
          <IonToolbar>
            <IonTitle>{t(settings.language, 'notificationTime')}</IonTitle>
            <IonButtons slot="end">
              <IonButton aria-label={t(settings.language, 'keyboard')} onClick={() => setKeyboardMode((value) => !value)}>
                <IonIcon icon={keypadOutline} />
              </IonButton>
              <IonButton onClick={saveTime}>{t(settings.language, 'save')}</IonButton>
              <IonButton onClick={() => setTimeOpen(false)}>{t(settings.language, 'cancel')}</IonButton>
            </IonButtons>
          </IonToolbar>
        </IonHeader>
        <IonContent className="ion-padding">
          <div className="clock-time" aria-live="polite">
            <button className={clockPart === 'hour' ? 'active' : ''} onClick={() => setClockPart('hour')}>{String(hour).padStart(2, '0')}</button>
            <span>:</span>
            <button className={clockPart === 'minute' ? 'active' : ''} onClick={() => setClockPart('minute')}>{String(minute).padStart(2, '0')}</button>
          </div>
          {keyboardMode && <div className="clock-keyboard-fields">
            <IonInput
              ref={hourInputRef}
              className={`clock-keyboard-input ${clockPart === 'hour' ? 'active' : ''}`}
              type="text"
              inputMode="numeric"
              maxlength={2}
              value={hourText}
              aria-label="HH"
              onFocus={() => setClockPart('hour')}
              onIonInput={(event) => {
                const value = String(event.detail.value ?? '').replace(/\D/g, '').slice(0, 2);
                const numericValue = Number(value);
                if (!value) {
                  setHourText('');
                } else if (numericValue <= 23) {
                  setHourText(value);
                  setDraftTime(`${value.padStart(2, '0')}:${String(minute).padStart(2, '0')}`);
                }
              }}
            />
            <span className="clock-keyboard-separator">:</span>
            <IonInput
              ref={minuteInputRef}
              className={`clock-keyboard-input ${clockPart === 'minute' ? 'active' : ''}`}
              type="text"
              inputMode="numeric"
              maxlength={2}
              value={minuteText}
              aria-label="mm"
              onFocus={() => setClockPart('minute')}
              onIonInput={(event) => {
                const value = String(event.detail.value ?? '').replace(/\D/g, '').slice(0, 2);
                const numericValue = Number(value);
                if (!value) {
                  setMinuteText('');
                } else if (numericValue <= 59) {
                  setMinuteText(value);
                  setDraftTime(`${String(hour).padStart(2, '0')}:${value.padStart(2, '0')}`);
                }
              }}
            />
          </div>}
          <div
            className="analog-clock"
            aria-label={t(settings.language, 'notificationTime')}
            onPointerDown={(event) => {
              event.preventDefault();
              draggingRef.current = true;
              event.currentTarget.setPointerCapture(event.pointerId);
              updateClockFromPointer(event);
            }}
            onPointerMove={(event) => { if (draggingRef.current && event.buttons !== 0) updateClockFromPointer(event); }}
            onPointerUp={(event) => {
              event.preventDefault();
              draggingRef.current = false;
              setClockPart(clockPart === 'hour' ? 'minute' : 'hour');
            }}
            onPointerCancel={() => { draggingRef.current = false; }}
            onLostPointerCapture={() => { draggingRef.current = false; }}
          >
            <span className="clock-hand" style={{ transform: `rotate(${clockPart === 'hour' ? (hour % 12) * 30 : minute * 6}deg)` }} />
            {clockValues.map((value, index) => {
              const isInnerHour = clockPart === 'hour' && value >= 12;
              const angle = clockPart === 'hour' ? (value % 12) * 30 : index * 30;
              const selected = clockPart === 'hour' ? value === hour : value === minute;
              return <button
                className={`${selected ? 'selected ' : ''}${isInnerHour ? 'inner-hour' : 'outer-hour'}`}
                key={`${clockPart}-${value}`}
                style={{ transform: `rotate(${angle}deg) translateY(${isInnerHour ? '-78px' : '-122px'}) rotate(${-angle}deg)` }}
                onClick={() => selectClockValue(value)}
              >{String(value).padStart(2, '0')}</button>;
            })}
          </div>
        </IonContent>
      </IonModal>
    </IonContent>
  </IonModal>;
}
