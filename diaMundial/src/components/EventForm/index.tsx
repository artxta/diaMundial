import { useState } from 'react';
import { IonButton, IonInput, IonItem, IonLabel, IonList, IonSelect, IonSelectOption, IonTextarea } from '@ionic/react';
import { categoryName } from '../../i18n';
import { categoryLabels, categorySymbols, classify, type Category, type Event } from '../../models';
import type { Language } from '../../models';
import { t } from '../../i18n';
import './EventForm.css';

const today = () => {
  const now = new Date();
  return `${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
};

interface EventFormProps {
  initial?: Event;
  onSave: (event: Event) => void;
  onCancel: () => void;
  language: Language;
}

export function EventForm({ initial, onSave, onCancel, language }: EventFormProps) {
  const [date, setDate] = useState(initial?.date ?? today());
  const [title, setTitle] = useState(initial?.title ?? '');
  const [description, setDescription] = useState(initial?.description ?? '');
  const [country, setCountry] = useState(initial?.country ?? 'España');
  const [image, setImage] = useState(initial?.image ?? '');
  const [url, setUrl] = useState(initial?.url ?? '');
  const [category, setCategory] = useState<Category>(initial?.category ?? (initial ? classify(initial) : 'culture'));

  return <IonList className="form">
    <IonItem><IonInput label={t(language, 'dateFormat')} value={date} onIonInput={(event) => setDate(String(event.detail.value ?? ''))} /></IonItem>
    <IonItem><IonInput label={t(language, 'name')} value={title} onIonInput={(event) => setTitle(String(event.detail.value ?? ''))} /></IonItem>
    <IonItem><IonTextarea label={t(language, 'description')} value={description} onIonInput={(event) => setDescription(String(event.detail.value ?? ''))} /></IonItem>
    <IonItem><IonInput label={t(language, 'country')} value={country} onIonInput={(event) => setCountry(String(event.detail.value ?? ''))} /></IonItem>
    <IonItem><IonInput type="url" label={t(language, 'image')} value={image} onIonInput={(event) => setImage(String(event.detail.value ?? ''))} /></IonItem>
    <IonItem><IonInput type="url" label={t(language, 'url')} value={url} onIonInput={(event) => setUrl(String(event.detail.value ?? ''))} /></IonItem>
    <IonItem>
      <IonLabel>{t(language, 'category')}</IonLabel>
      <IonSelect value={category} onIonChange={(event) => setCategory(event.detail.value as Category)} interface="modal">
        {(Object.keys(categoryLabels) as Category[]).map((item) => <IonSelectOption key={item} value={item}>{categorySymbols[item]} {categoryName(language, item)}</IonSelectOption>)}
      </IonSelect>
    </IonItem>
    <div className="form-actions">
      <IonButton onClick={() => onSave({ date, title, description, country, image, url, category, custom: true })}>{t(language, 'save')}</IonButton>
      <IonButton fill="outline" onClick={onCancel}>{t(language, 'cancel')}</IonButton>
    </div>
  </IonList>;
}
