import { IonButton, IonSearchbar } from '@ionic/react';
import { useEffect, useState } from 'react';
import { t } from '../../i18n';
import './DaySearch.css';
import type { Language } from '../../models';

interface DaySearchProps {
  value: string;
  language: Language;
  onSearch: (value: string) => void;
}

export function DaySearch({ value, language, onSearch }: DaySearchProps) {
  const [query, setQuery] = useState(value);

  useEffect(() => setQuery(value), [value]);

  return (
    <form className="day-search" onSubmit={(event) => { event.preventDefault(); onSearch(query); }}>
      <IonSearchbar
        value={query}
        placeholder={t(language, 'search')}
        onIonInput={(event) => setQuery(event.detail.value ?? '')}
        aria-label={t(language, 'search')}
      />
      <IonButton type="submit">{t(language, 'searchButton')}</IonButton>
    </form>
  );
}
