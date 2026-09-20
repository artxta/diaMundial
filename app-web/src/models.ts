export interface Event {
  date: string;
  title: string;
  description: string;
  country: string;
  image?: string;
  url?: string;
  category?: Category;
  custom?: boolean;
}

export type Language = 'system' | 'es' | 'en' | 'fr' | 'zh' | 'ja' | 'pt' | 'ca' | 'ru' | 'ro' | 'ar';
export type Theme = 'light' | 'dark' | 'system';
export type Category = 'health' | 'education' | 'environment' | 'culture' | 'science_tech';
export type NotificationMode = 'none' | 'silent' | 'sound';
export type NotificationDay = 0 | 1 | 2 | 3 | 4 | 5 | 6;
export type FontFamily = 'system' | 'roboto' | 'serif' | 'mono' | 'rounded' | 'cursive' | 'handwriting';

export interface AppSettings {
  language: Language;
  theme: Theme;
  fontScale: number;
  fontFamily: FontFamily;
  notificationMode: NotificationMode;
  notificationTime: string;
  notificationDays: NotificationDay[];
  category: 'all' | Category;
  favorites: string[];
}

export const languageNames: Record<Language, string> = {
  system: 'System default',
  es: 'Español',
  en: 'English',
  fr: 'Français',
  zh: '中文',
  ja: '日本語',
  ru: 'Русский',
  ro: 'Română',
  ar: 'العربية',
  pt: 'Português',
  ca: 'Català'
};

export const categoryLabels: Record<Category, string> = {
  health: 'Salud', education: 'Educación', environment: 'Medio ambiente',
  culture: 'Cultura', science_tech: 'Ciencia y tecnología'
};

export const categorySymbols: Record<Category, string> = {
  health: '♥',
  education: '🎓',
  environment: '🍃',
  culture: '🎨',
  science_tech: '⚗'
};

const keywords: Record<Category, string[]> = {
  health: ['salud', 'health', 'enfermedad', 'disease', 'cáncer', 'cancer', 'médic', 'medical', 'aliment'],
  education: ['educación', 'education', 'escuela', 'school', 'universidad', 'university', 'libro', 'book'],
  environment: ['ambiente', 'environment', 'clima', 'climate', 'bosque', 'forest', 'agua', 'water', 'océano', 'ocean', 'animal', 'biodivers'],
  culture: ['cultura', 'culture', 'arte', 'art', 'música', 'music', 'paz', 'peace', 'derechos', 'rights', 'mujer', 'woman'],
  science_tech: ['ciencia', 'science', 'tecnología', 'technology', 'espacio', 'space', 'matemát', 'mathematic', 'digital']
};

export function classify(event: Event): Category {
  if (event.category) return event.category;
  const text = `${event.title} ${event.description}`.toLocaleLowerCase();
  let best: Category = 'culture';
  let score = 0;
  for (const category of Object.keys(keywords) as Category[]) {
    const current = keywords[category].filter((word) => text.includes(word)).length;
    if (current > score) { best = category; score = current; }
  }
  return best;
}
