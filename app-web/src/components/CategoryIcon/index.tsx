import { IonIcon } from '@ionic/react';
import {
  colorPaletteOutline,
  flaskOutline,
  heartOutline,
  leafOutline,
  schoolOutline
} from 'ionicons/icons';
import type { Category } from '../../models';
import './CategoryIcon.css';

const categoryIcons = {
  health: heartOutline,
  education: schoolOutline,
  environment: leafOutline,
  culture: colorPaletteOutline,
  science_tech: flaskOutline
} satisfies Record<Category, string>;

interface CategoryIconProps {
  category: Category;
  label?: string;
}

export function CategoryIcon({ category, label }: CategoryIconProps) {
  return <IonIcon
    className={`category-icon category-icon-${category}`}
    icon={categoryIcons[category]}
    aria-label={label}
    aria-hidden={label ? undefined : true}
  />;
}
