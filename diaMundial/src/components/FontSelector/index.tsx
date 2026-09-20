import { IonItem, IonLabel, IonSelect, IonSelectOption } from '@ionic/react';
import type { FontFamily, Language } from '../../models';
import './FontSelector.css';
import { t } from '../../i18n';

interface FontSelectorProps {
  value: FontFamily;
  language: Language;
  onChange: (fontFamily: FontFamily) => void;
}

const fonts: Array<{ value: FontFamily; label: string }> = [
  { value: 'system', label: 'System' },
  { value: 'roboto', label: 'Roboto' },
  { value: 'rounded', label: 'Nunito' },
  { value: 'serif', label: 'Serif' },
  { value: 'mono', label: 'Monospace' },
  { value: 'cursive', label: 'Cursive' },
  { value: 'handwriting', label: 'Handwriting' }
];

export function FontSelector({ value, language, onChange }: FontSelectorProps) {
  return <IonItem>
    <IonLabel>{t(language, 'fontFamily')}</IonLabel>
    <IonSelect value={value} onIonChange={(event) => onChange(event.detail.value as FontFamily)} interface="modal" interfaceOptions={{ header: `Aa ${t(language, 'fontFamily')}` }}>
      {fonts.map((font) => <IonSelectOption key={font.value} value={font.value}>Aa {font.label}</IonSelectOption>)}
    </IonSelect>
  </IonItem>;
}
