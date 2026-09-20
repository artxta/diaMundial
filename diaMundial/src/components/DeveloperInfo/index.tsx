import { IonItem, IonLabel, IonList, IonText } from '@ionic/react';
import { t } from '../../i18n';
import type { Language } from '../../models';
import './DeveloperInfo.css';

interface DeveloperInfoProps {
  language: Language;
}

export function DeveloperInfo({ language }: DeveloperInfoProps) {
  return <section className="settings-card settings-card-developer">
    <IonText><h2>{t(language, 'developerInfo')}</h2></IonText>
    <IonList>
      <IonItem>
        <IonLabel>
          <h3>José Alberto Torresano</h3>
          <p>{t(language, 'version')}</p>

        </IonLabel>
      </IonItem>
    </IonList>
  </section>;
}
