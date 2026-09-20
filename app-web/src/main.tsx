import React from 'react';
import { createRoot } from 'react-dom/client';
import { IonApp, setupIonicReact } from '@ionic/react';
import '@ionic/react/css/core.css';
import '@ionic/react/css/normalize.css';
import '@ionic/react/css/structure.css';
import '@ionic/react/css/typography.css';
import './theme.css';
import { App } from './App';

setupIonicReact({ mode: 'md' });
createRoot(document.getElementById('root')!).render(<React.StrictMode><IonApp><App /></IonApp></React.StrictMode>);
