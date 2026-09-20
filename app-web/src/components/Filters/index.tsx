import { IonSelect, IonSelectOption } from '@ionic/react';
import { categoryName, t } from '../../i18n';
import { categoryLabels, categorySymbols, type AppSettings, type Category, type Language } from '../../models';
import { CategoryIcon } from '../CategoryIcon';
import './Filters.css';

const countryFlags: Record<string, string> = {
  global: '🌐',
  españa: '🇪🇸',
  spain: '🇪🇸',
  portugal: '🇵🇹',
  francia: '🇫🇷',
  france: '🇫🇷',
  italia: '🇮🇹',
  italy: '🇮🇹',
  alemania: '🇩🇪',
  germany: '🇩🇪',
  'reino unido': '🇬🇧',
  'united kingdom': '🇬🇧',
  'estados unidos': '🇺🇸',
  'united states': '🇺🇸',
  méxico: '🇲🇽',
  mexico: '🇲🇽',
  brasil: '🇧🇷',
  brazil: '🇧🇷',
  argentina: '🇦🇷',
  china: '🇨🇳',
  japón: '🇯🇵',
  japan: '🇯🇵',
  india: '🇮🇳',
  australia: '🇦🇺'
};

function countryLabel(country: string): string {
  const flag = countryFlags[country.trim().toLocaleLowerCase()] ?? '🏳️';
  return `${flag} ${country}`;
}

interface FiltersProps {
  country: string;
  countries: string[];
  category: AppSettings['category'];
  language: Language;
  onCountryChange: (country: string) => void;
  onCategoryChange: (category: AppSettings['category']) => void;
}

export function Filters({
  country,
  countries,
  category,
  language,
  onCountryChange,
  onCategoryChange
}: FiltersProps) {
  return <div className="filters">
    <IonSelect
      value={country}
      onIonChange={(event) => onCountryChange(String(event.detail.value))}
      interface="modal"
      aria-label={t(language, 'country')}
    >
      <IonSelectOption value="all">🌐 {t(language, 'allCountries')}</IonSelectOption>
      {countries.map((item) => <IonSelectOption key={item} value={item}>{countryLabel(item)}</IonSelectOption>)}
    </IonSelect>
    <IonSelect
      value={category}
      onIonChange={(event) => onCategoryChange(event.detail.value as AppSettings['category'])}
      interface="modal"
      aria-label={t(language, 'filterByCategory')}
    >
      <IonSelectOption value="all">◉ {t(language, 'allCategories')}</IonSelectOption>
      {(Object.keys(categoryLabels) as Category[]).map((item) => <IonSelectOption key={item} value={item}>{categorySymbols[item]} {categoryName(language, item)}</IonSelectOption>)}
    </IonSelect>
  </div>;
}
