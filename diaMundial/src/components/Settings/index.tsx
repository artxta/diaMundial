import { IonItem, IonLabel, IonList, IonSelect, IonSelectOption, IonText } from '@ionic/react';
import { categoryLabels, categorySymbols, languageNames, type AppSettings, type Category, type Language, type Theme } from '../../models';
import { categoryName, t } from '../../i18n';
import { FontSelector } from '../FontSelector';
import { NotificationSettings } from '../NotificationSettings';
import { DeveloperInfo } from '../DeveloperInfo';
import './Settings.css';

const languageFlags: Record<Language, string> = {
  system: '🌐',
  es: '🇪🇸',
  en: '🇬🇧',
  fr: '🇫🇷',
  pt: '🇵🇹',
  ca: '🟨🟥🟨🟥',
  zh: '🇨🇳',
  ja: '🇯🇵',
  ru: '🇷🇺',
  ro: '🇷🇴',
  ar: '🇸🇦'
};

interface SettingsProps {
  settings: AppSettings;
  update: (patch: Partial<AppSettings>) => void;
  onOpenNotifications?: () => void;
}

export function Settings({ settings, update, onOpenNotifications }: SettingsProps) {
  return <div className="settings">
    <section className="settings-card settings-card-appearance">
      <IonText><h2>{t(settings.language, 'appearance')}</h2></IonText>
      <IonList>
        <IonItem><IonLabel>{t(settings.language, 'appTheme')}</IonLabel><IonSelect interface="modal" interfaceOptions={{ header: `☀ ${t(settings.language, 'appTheme')}` }} value={settings.theme} onIonChange={(event) => update({ theme: event.detail.value as Theme })}><IonSelectOption value="system">⚙ {t(settings.language, 'systemTheme')}</IonSelectOption><IonSelectOption value="light">☀ {t(settings.language, 'lightTheme')}</IonSelectOption><IonSelectOption value="dark">☾ {t(settings.language, 'darkTheme')}</IonSelectOption></IonSelect></IonItem>
        <IonItem><IonLabel>{t(settings.language, 'fontSize')}</IonLabel><IonSelect interface="modal" interfaceOptions={{ header: `A ${t(settings.language, 'fontSize')}` }} value={settings.fontScale} onIonChange={(event) => update({ fontScale: Number(event.detail.value) })}><IonSelectOption value={0.9}>A− {t(settings.language, 'small')}</IonSelectOption><IonSelectOption value={1}>A {t(settings.language, 'normal')}</IonSelectOption><IonSelectOption value={1.15}>A+ {t(settings.language, 'large')}</IonSelectOption><IonSelectOption value={1.3}>A++ {t(settings.language, 'extraLarge')}</IonSelectOption></IonSelect></IonItem>
        <FontSelector value={settings.fontFamily} language={settings.language} onChange={(fontFamily) => update({ fontFamily })} />
      </IonList>
    </section>
    <section className="settings-card settings-card-language">
      <IonText><h2>{t(settings.language, 'language')}</h2></IonText>
      <IonList><IonItem><IonLabel>{t(settings.language, 'language')}</IonLabel><IonSelect interface="modal" interfaceOptions={{ header: `🌐 ${t(settings.language, 'language')}` }} value={settings.language} onIonChange={(event) => update({ language: event.detail.value as Language })}>{(Object.keys(languageNames) as Language[]).map((language) => <IonSelectOption key={language} value={language}>{languageFlags[language]} {languageNames[language]}</IonSelectOption>)}</IonSelect></IonItem></IonList>
    </section>
    <section className="settings-card settings-card-notifications">
      <IonText><h2>{t(settings.language, 'notifications')}</h2></IonText>
      <IonList><IonItem button onClick={onOpenNotifications}><IonLabel>{t(settings.language, 'notificationMode')}</IonLabel><IonLabel slot="end">{t(settings.language, settings.notificationMode === 'none' ? 'noNotification' : settings.notificationMode === 'silent' ? 'silentNotification' : 'soundNotification')}</IonLabel></IonItem></IonList>
    </section>
    <section className="settings-card settings-card-categories">
      <IonText><h2>{t(settings.language, 'categories')}</h2></IonText>
      <IonList><IonItem><IonLabel>{t(settings.language, 'filterByCategory')}</IonLabel><IonSelect interface="modal" interfaceOptions={{ header: `◉ ${t(settings.language, 'filterByCategory')}` }} value={settings.category} onIonChange={(event) => update({ category: event.detail.value as AppSettings['category'] })}><IonSelectOption value="all">◉ {t(settings.language, 'allCategories')}</IonSelectOption>{(Object.keys(categoryLabels) as Category[]).map((category) => <IonSelectOption key={category} value={category}>{categorySymbols[category]} {categoryName(settings.language, category)}</IonSelectOption>)}</IonSelect></IonItem></IonList>
    </section>
    <DeveloperInfo language={settings.language} />
  </div>;
}
