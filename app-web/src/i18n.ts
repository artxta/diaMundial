import type { Language } from './models';

export type TranslationKey = keyof typeof translations.es;

const translations = {
  es: {
    appTitle: 'Día Mundial', favorites: 'Favoritos', settings: 'Ajustes', home: 'Inicio',
    add: 'Añadir', close: 'Cerrar', edit: 'Editar', share: 'Compartir', shareWhatsApp: 'Añadir como estado de WhatsApp', shareTelegram: 'Telegram', copy: 'Copiar', shareMore: 'Más opciones', addFavorite: 'Añadir a favoritos',
    removeFavorite: 'Quitar de favoritos', date: 'Fecha', category: 'Categoría', celebration: 'Celebración',
    selectedDay: 'Día seleccionado', celebrations: 'celebraciones', noCelebrations: 'Sin celebraciones',
    noCelebrationsForDay: 'No hay celebraciones para este día.', createCustomDay: 'Crear día personalizado',
    todayCelebrations: 'Celebraciones de hoy', noCelebrationsToday: 'No hay celebraciones para hoy.',
    search: 'Buscar celebraciones', searchButton: 'Buscar', allCountries: 'Todos los países', allCategories: 'Todas las categorías',
    noFilterResults: 'No hay celebraciones para los filtros seleccionados.', appearance: 'Apariencia',
    appTheme: 'Tema de la aplicación', systemTheme: 'Según el sistema', lightTheme: 'Modo claro', darkTheme: 'Modo oscuro',
    fontSize: 'Tamaño de fuente', fontFamily: 'Tipo de fuente', small: 'Pequeña', normal: 'Normal', large: 'Grande', extraLarge: 'Extra grande',
    language: 'Idioma', notifications: 'Notificaciones', notificationMode: 'Modo de notificación', notificationTime: 'Hora', keyboard: 'Teclado', hours: 'Horas', minutes: 'Minutos', notificationDays: 'Días de notificación', monday: 'Lunes', tuesday: 'Martes', wednesday: 'Miércoles', thursday: 'Jueves', friday: 'Viernes', saturday: 'Sábado', sunday: 'Domingo',
    noNotification: 'Sin notificación', silentNotification: 'Notificación silenciosa', soundNotification: 'Notificación con sonido',
    categories: 'Categorías', filterByCategory: 'Filtrar por categoría', developerInfo: 'Información del desarrollador',
    version: 'Versión 1.0.0-12', dateFormat: 'Fecha (MM-DD)', name: 'Nombre', description: 'Descripción',
    country: 'País', image: 'Imagen (opcional)', url: 'URL (opcional)', save: 'Guardar', cancel: 'Cancelar', newCelebration: 'Nueva celebración',
    editCelebration: 'Editar celebración', tourTitle: 'Guía rápida', tourSkip: 'Saltar', tourNext: 'Siguiente', tourFinish: 'Terminar',
    tourTodayTitle: 'Celebraciones de hoy', tourTodayDescription: 'Aquí puedes ver las celebraciones que ocurren hoy.',
    tourSearchTitle: 'Buscar días', tourSearchDescription: 'Escribe palabras clave y pulsa Buscar para encontrar celebraciones por título o descripción.',
    tourFiltersTitle: 'Filtros', tourFiltersDescription: 'Filtra las celebraciones por país y categoría.',
    tourCalendarTitle: 'Calendario', tourCalendarDescription: 'Selecciona un día o mantén pulsado para crear una celebración personalizada.',
    tourResultsTitle: 'Resultados', tourResultsDescription: 'Las celebraciones aparecen en esta lista.',
    tourNotificationsTitle: 'Notificaciones', tourNotificationsDescription: 'Toca la campana para configurar las notificaciones.',
    tourNavigationTitle: 'Navegación', tourNavigationDescription: 'Usa Inicio, Favoritos y Ajustes para moverte por la aplicación.'
  },
  en: {
    appTitle: 'World Days', favorites: 'Favorites', settings: 'Settings', home: 'Home', add: 'Add', close: 'Close',
    edit: 'Edit', share: 'Share', shareWhatsApp: 'Add as WhatsApp Status', shareTelegram: 'Telegram', shareMore: 'More options', addFavorite: 'Add to favorites', removeFavorite: 'Remove from favorites',
    date: 'Date', category: 'Category', celebration: 'Celebration', selectedDay: 'Selected day', celebrations: 'celebrations',
    noCelebrations: 'No celebrations', noCelebrationsForDay: 'There are no celebrations on this day.',
    createCustomDay: 'Create custom day', todayCelebrations: "Today's celebrations", noCelebrationsToday: 'No celebrations today.',
    search: 'Search celebrations', searchButton: 'Search', allCountries: 'All countries', allCategories: 'All categories',
    noFilterResults: 'No celebrations match the selected filters.', appearance: 'Appearance', appTheme: 'App theme',
    systemTheme: 'System default', lightTheme: 'Light mode', darkTheme: 'Dark mode', fontSize: 'Font size', fontFamily: 'Font family',
    small: 'Small', normal: 'Normal', large: 'Large', extraLarge: 'Extra large', language: 'Language',
    notifications: 'Notifications', notificationMode: 'Notification mode', notificationTime: 'Time', keyboard: 'Keyboard', hours: 'Hours', minutes: 'Minutes', notificationDays: 'Notification days', monday: 'Monday', tuesday: 'Tuesday', wednesday: 'Wednesday', thursday: 'Thursday', friday: 'Friday', saturday: 'Saturday', sunday: 'Sunday', noNotification: 'No notification',
    silentNotification: 'Silent notification', soundNotification: 'Notification with sound', categories: 'Categories',
    filterByCategory: 'Filter by category', developerInfo: 'Developer information', version: 'Version 1.0.0-12',
    dateFormat: 'Date (MM-DD)', name: 'Name', description: 'Description', country: 'Country', save: 'Save',
    cancel: 'Cancel', newCelebration: 'New celebration', editCelebration: 'Edit celebration', tourTitle: 'Quick tour', tourSkip: 'Skip', tourNext: 'Next', tourFinish: 'Finish',
    tourTodayTitle: "Today's celebrations", tourTodayDescription: 'See the celebrations that take place today.', tourSearchTitle: 'Search days', tourSearchDescription: 'Find celebrations by title or description.', tourFiltersTitle: 'Filters', tourFiltersDescription: 'Filter by country and category.', tourCalendarTitle: 'Calendar', tourCalendarDescription: 'Select a day or long-press to create a custom celebration.', tourResultsTitle: 'Results', tourResultsDescription: 'Celebrations appear in this list.', tourNotificationsTitle: 'Notifications', tourNotificationsDescription: 'Tap the bell to configure notifications.', tourNavigationTitle: 'Navigation', tourNavigationDescription: 'Use Home, Favorites and Settings to move around the app.'
  },
  fr: {
    appTitle: 'Jours mondiaux', favorites: 'Favoris', settings: 'Paramètres', home: 'Accueil', add: 'Ajouter',
    close: 'Fermer', edit: 'Modifier', share: 'Partager', shareWhatsApp: 'Ajouter au statut WhatsApp', shareTelegram: 'Telegram', shareMore: 'Plus d’options', addFavorite: 'Ajouter aux favoris',
    removeFavorite: 'Retirer des favoris', date: 'Date', category: 'Catégorie', celebration: 'Célébration',
    selectedDay: 'Jour sélectionné', celebrations: 'célébrations', noCelebrations: 'Aucune célébration',
    noCelebrationsForDay: 'Aucune célébration ce jour.', createCustomDay: 'Créer un jour personnalisé',
    todayCelebrations: "Célébrations d'aujourd'hui", noCelebrationsToday: "Aucune célébration aujourd'hui.",
    search: 'Rechercher des célébrations', searchButton: 'Rechercher', allCountries: 'Tous les pays', allCategories: 'Toutes les catégories',
    noFilterResults: 'Aucune célébration ne correspond aux filtres.', appearance: 'Apparence', appTheme: "Thème de l'application",
    systemTheme: 'Selon le système', lightTheme: 'Mode clair', darkTheme: 'Mode sombre', fontSize: 'Taille du texte', fontFamily: 'Police',
    small: 'Petite', normal: 'Normale', large: 'Grande', extraLarge: 'Très grande', language: 'Langue',
    notifications: 'Notifications', notificationMode: 'Mode de notification', notificationTime: 'Heure', keyboard: 'Clavier', hours: 'Heures', minutes: 'Minutes', notificationDays: 'Jours de notification', monday: 'Lundi', tuesday: 'Mardi', wednesday: 'Mercredi', thursday: 'Jeudi', friday: 'Vendredi', saturday: 'Samedi', sunday: 'Dimanche', noNotification: 'Aucune notification',
    silentNotification: 'Notification silencieuse', soundNotification: 'Notification sonore', categories: 'Catégories',
    filterByCategory: 'Filtrer par catégorie', developerInfo: 'Informations du développeur', version: 'Version 1.0.0-12',
    dateFormat: 'Date (MM-JJ)', name: 'Nom', description: 'Description', country: 'Pays', save: 'Enregistrer',
    cancel: 'Annuler', newCelebration: 'Nouvelle célébration', editCelebration: 'Modifier la célébration', tourTitle: 'Visite guidée', tourSkip: 'Passer', tourNext: 'Suivant', tourFinish: 'Terminer', tourTodayTitle: 'Célébrations du jour', tourTodayDescription: 'Consultez les célébrations du jour.', tourSearchTitle: 'Rechercher', tourSearchDescription: 'Trouvez des célébrations par titre ou description.', tourFiltersTitle: 'Filtres', tourFiltersDescription: 'Filtrez par pays et catégorie.', tourCalendarTitle: 'Calendrier', tourCalendarDescription: 'Sélectionnez un jour ou maintenez la pression pour créer une célébration.', tourResultsTitle: 'Résultats', tourResultsDescription: 'Les célébrations apparaissent dans cette liste.', tourNotificationsTitle: 'Notifications', tourNotificationsDescription: 'Touchez la cloche pour configurer les notifications.', tourNavigationTitle: 'Navigation', tourNavigationDescription: 'Utilisez Accueil, Favoris et Paramètres.'
  },
  pt: {
    appTitle: 'Dias Mundiais', favorites: 'Favoritos', settings: 'Configurações', home: 'Início', add: 'Adicionar',
    close: 'Fechar', edit: 'Editar', share: 'Partilhar', shareWhatsApp: 'Adicionar ao estado do WhatsApp', shareTelegram: 'Telegram', shareMore: 'Mais opções', addFavorite: 'Adicionar aos favoritos',
    removeFavorite: 'Remover dos favoritos', date: 'Data', category: 'Categoria', celebration: 'Celebração',
    selectedDay: 'Dia selecionado', celebrations: 'celebrações', noCelebrations: 'Sem celebrações',
    noCelebrationsForDay: 'Não há celebrações neste dia.', createCustomDay: 'Criar dia personalizado',
    todayCelebrations: 'Celebrações de hoje', noCelebrationsToday: 'Não há celebrações hoje.',
    search: 'Pesquisar celebrações', searchButton: 'Pesquisar', allCountries: 'Todos os países', allCategories: 'Todas as categorias',
    noFilterResults: 'Não há celebrações para os filtros selecionados.', appearance: 'Aparência',
    appTheme: 'Tema da aplicação', systemTheme: 'Padrão do sistema', lightTheme: 'Modo claro', darkTheme: 'Modo escuro',
    fontSize: 'Tamanho da fonte', fontFamily: 'Tipo de letra', small: 'Pequena', normal: 'Normal', large: 'Grande', extraLarge: 'Extra grande',
    language: 'Idioma', notifications: 'Notificações', notificationMode: 'Modo de notificação', notificationTime: 'Hora', keyboard: 'Teclado', notificationDays: 'Dias de notificação', monday: 'Segunda-feira', tuesday: 'Terça-feira', wednesday: 'Quarta-feira', thursday: 'Quinta-feira', friday: 'Sexta-feira', saturday: 'Sábado', sunday: 'Domingo',
    noNotification: 'Sem notificação', silentNotification: 'Notificação silenciosa', soundNotification: 'Notificação com som',
    categories: 'Categorias', filterByCategory: 'Filtrar por categoria', developerInfo: 'Informação do programador',
    version: 'Versão 1.0.0-12', dateFormat: 'Data (MM-DD)', name: 'Nome', description: 'Descrição', country: 'País',
    save: 'Guardar', cancel: 'Cancelar', newCelebration: 'Nova celebração', editCelebration: 'Editar celebração', tourTitle: 'Visita rápida', tourSkip: 'Saltar', tourNext: 'Seguinte', tourFinish: 'Concluir', tourTodayTitle: 'Celebrações de hoje', tourTodayDescription: 'Veja as celebrações de hoje.', tourSearchTitle: 'Pesquisar', tourSearchDescription: 'Encontre celebrações pelo título ou descrição.', tourFiltersTitle: 'Filtros', tourFiltersDescription: 'Filtre por país e categoria.', tourCalendarTitle: 'Calendário', tourCalendarDescription: 'Selecione um dia ou mantenha premido para criar uma celebração.', tourResultsTitle: 'Resultados', tourResultsDescription: 'As celebrações aparecem nesta lista.', tourNotificationsTitle: 'Notificações', tourNotificationsDescription: 'Toque no sino para configurar as notificações.', tourNavigationTitle: 'Navegação', tourNavigationDescription: 'Use Início, Favoritos e Configurações.'
  },
  ca: {
    appTitle: 'Dies mundials', favorites: 'Preferits', settings: 'Configuració', home: 'Inici', add: 'Afegir',
    close: 'Tancar', edit: 'Editar', share: 'Compartir', shareWhatsApp: 'Afegir com a estat de WhatsApp', shareTelegram: 'Telegram', shareMore: 'Més opcions', addFavorite: 'Afegir als preferits',
    removeFavorite: 'Treure dels preferits', date: 'Data', category: 'Categoria', celebration: 'Celebració',
    selectedDay: 'Dia seleccionat', celebrations: 'celebracions', noCelebrations: 'Sense celebracions',
    noCelebrationsForDay: 'No hi ha celebracions aquest dia.', createCustomDay: 'Crear un dia personalitzat',
    todayCelebrations: "Celebracions d'avui", noCelebrationsToday: 'No hi ha celebracions avui.',
    search: 'Cercar celebracions', searchButton: 'Cercar', allCountries: 'Tots els països', allCategories: 'Totes les categories',
    noFilterResults: 'No hi ha celebracions per als filtres seleccionats.', appearance: 'Aparença',
    appTheme: "Tema de l'aplicació", systemTheme: 'Segons el sistema', lightTheme: 'Mode clar', darkTheme: 'Mode fosc',
    fontSize: 'Mida de la lletra', fontFamily: 'Tipus de lletra', small: 'Petita', normal: 'Normal', large: 'Gran', extraLarge: 'Extra gran',
    language: 'Idioma', notifications: 'Notificacions', notificationMode: 'Mode de notificació', notificationTime: 'Hora', keyboard: 'Teclat', notificationDays: 'Dies de notificació', monday: 'Dilluns', tuesday: 'Dimarts', wednesday: 'Dimecres', thursday: 'Dijous', friday: 'Divendres', saturday: 'Dissabte', sunday: 'Diumenge',
    noNotification: 'Sense notificació', silentNotification: 'Notificació silenciosa', soundNotification: 'Notificació amb so',
    categories: 'Categories', filterByCategory: 'Filtrar per categoria', developerInfo: 'Informació del desenvolupador',
    version: 'Versió 1.0.0-12', dateFormat: 'Data (MM-DD)', name: 'Nom', description: 'Descripció', country: 'País',
    save: 'Desar', cancel: 'Cancel·lar', newCelebration: 'Nova celebració', editCelebration: 'Editar celebració', tourTitle: 'Guia ràpida', tourSkip: 'Ometre', tourNext: 'Següent', tourFinish: 'Finalitzar', tourTodayTitle: "Celebracions d'avui", tourTodayDescription: 'Consulta les celebracions d’avui.', tourSearchTitle: 'Cercar', tourSearchDescription: 'Troba celebracions pel títol o la descripció.', tourFiltersTitle: 'Filtres', tourFiltersDescription: 'Filtra per país i categoria.', tourCalendarTitle: 'Calendari', tourCalendarDescription: 'Selecciona un dia o mantén premut per crear una celebració.', tourResultsTitle: 'Resultats', tourResultsDescription: 'Les celebracions apareixen en aquesta llista.', tourNotificationsTitle: 'Notificacions', tourNotificationsDescription: 'Toca la campana per configurar les notificacions.', tourNavigationTitle: 'Navegació', tourNavigationDescription: 'Utilitza Inici, Preferits i Configuració.'
  },
  zh: {
    appTitle: '世界日', favorites: '收藏', settings: '设置', home: '首页', add: '添加', close: '关闭', edit: '编辑',
    share: '分享', shareWhatsApp: '添加到 WhatsApp 状态', shareTelegram: 'Telegram', shareMore: '更多选项', addFavorite: '添加到收藏', removeFavorite: '取消收藏', date: '日期', category: '类别',
    celebration: '纪念日', selectedDay: '选定日期', celebrations: '个纪念日', noCelebrations: '没有纪念日',
    noCelebrationsForDay: '这一天没有纪念日。', createCustomDay: '创建自定义纪念日', todayCelebrations: '今日纪念日',
    noCelebrationsToday: '今天没有纪念日。', search: '搜索纪念日', searchButton: '搜索', allCountries: '所有国家', allCategories: '所有类别',
    noFilterResults: '没有符合筛选条件的纪念日。', appearance: '外观', appTheme: '应用主题', systemTheme: '跟随系统',
    lightTheme: '浅色模式', darkTheme: '深色模式', fontSize: '字体大小', fontFamily: '字体', small: '小', normal: '标准', large: '大',
    extraLarge: '特大', language: '语言',     notifications: '通知', notificationMode: '通知模式', notificationTime: '时间', keyboard: '键盘', notificationDays: '通知日期', monday: '星期一', tuesday: '星期二', wednesday: '星期三', thursday: '星期四', friday: '星期五', saturday: '星期六', sunday: '星期日', noNotification: '无通知',
    silentNotification: '静默通知', soundNotification: '声音通知', categories: '类别', filterByCategory: '按类别筛选',
    developerInfo: '开发者信息', version: '版本 1.0.0-12', dateFormat: '日期 (MM-DD)', name: '名称', description: '描述',
    country: '国家', save: '保存', cancel: '取消', newCelebration: '新纪念日', editCelebration: '编辑纪念日', tourTitle: '快速导览', tourSkip: '跳过', tourNext: '下一步', tourFinish: '完成', tourTodayTitle: '今日纪念日', tourTodayDescription: '查看今天的纪念日。', tourSearchTitle: '搜索', tourSearchDescription: '按标题或描述查找纪念日。', tourFiltersTitle: '筛选', tourFiltersDescription: '按国家和类别筛选。', tourCalendarTitle: '日历', tourCalendarDescription: '选择日期或长按创建自定义纪念日。', tourResultsTitle: '结果', tourResultsDescription: '纪念日显示在此列表中。', tourNotificationsTitle: '通知', tourNotificationsDescription: '点击铃铛配置通知。', tourNavigationTitle: '导航', tourNavigationDescription: '使用首页、收藏和设置。'
  },
  ja: {
    appTitle: '世界の日', favorites: 'お気に入り', settings: '設定', home: 'ホーム', add: '追加', close: '閉じる',
    edit: '編集', share: '共有', shareWhatsApp: 'WhatsApp ステータスに追加', shareTelegram: 'Telegram', shareMore: 'その他のオプション', addFavorite: 'お気に入りに追加', removeFavorite: 'お気に入りから削除',
    date: '日付', category: 'カテゴリ', celebration: '記念日', selectedDay: '選択した日', celebrations: '件の記念日',
    noCelebrations: '記念日はありません', noCelebrationsForDay: 'この日に記念日はありません。',
    createCustomDay: 'カスタム記念日を作成', todayCelebrations: '今日の記念日', noCelebrationsToday: '今日の記念日はありません。',
    search: '記念日を検索', searchButton: '検索', allCountries: 'すべての国', allCategories: 'すべてのカテゴリ',
    noFilterResults: '選択した条件に一致する記念日はありません。', appearance: '外観', appTheme: 'アプリのテーマ',
    systemTheme: 'システムに従う', lightTheme: 'ライトモード', darkTheme: 'ダークモード', fontSize: '文字サイズ', fontFamily: 'フォント',
    small: '小', normal: '標準', large: '大', extraLarge: '特大', language: '言語', notifications: '通知',
    notificationMode: '通知モード', notificationTime: '時刻', keyboard: 'キーボード', notificationDays: '通知する曜日', monday: '月曜日', tuesday: '火曜日', wednesday: '水曜日', thursday: '木曜日', friday: '金曜日', saturday: '土曜日', sunday: '日曜日', noNotification: '通知なし', silentNotification: 'サイレント通知',
    soundNotification: '音付き通知', categories: 'カテゴリ', filterByCategory: 'カテゴリでフィルター',
    developerInfo: '開発者情報', version: 'バージョン 1.0.0-12', dateFormat: '日付 (MM-DD)', name: '名前',
    description: '説明', country: '国', save: '保存', cancel: 'キャンセル', newCelebration: '新しい記念日',
    editCelebration: '記念日を編集', tourTitle: 'クイックツアー', tourSkip: 'スキップ', tourNext: '次へ', tourFinish: '完了', tourTodayTitle: '今日の記念日', tourTodayDescription: '今日の記念日を確認できます。', tourSearchTitle: '検索', tourSearchDescription: 'タイトルまたは説明で記念日を検索します。', tourFiltersTitle: 'フィルター', tourFiltersDescription: '国とカテゴリで絞り込みます。', tourCalendarTitle: 'カレンダー', tourCalendarDescription: '日を選択するか長押ししてカスタム記念日を作成します。', tourResultsTitle: '結果', tourResultsDescription: '記念日はこの一覧に表示されます。', tourNotificationsTitle: '通知', tourNotificationsDescription: 'ベルをタップして通知を設定します。', tourNavigationTitle: 'ナビゲーション', tourNavigationDescription: 'ホーム、お気に入り、設定を使用します。'
  }
} as const;

const additionalTranslations = {
  ru: {
    ...translations.en,
    appTitle: 'Мировые дни', favorites: 'Избранное', settings: 'Настройки', home: 'Главная', add: 'Добавить', close: 'Закрыть',
    edit: 'Изменить', share: 'Поделиться', category: 'Категория', date: 'Дата', language: 'Язык', notifications: 'Уведомления',
    notificationMode: 'Режим уведомлений', notificationTime: 'Время', notificationDays: 'Дни уведомлений', noNotification: 'Без уведомлений',
    silentNotification: 'Без звука', soundNotification: 'Со звуком', search: 'Поиск праздников', searchButton: 'Искать',
    allCountries: 'Все страны', allCategories: 'Все категории', save: 'Сохранить', cancel: 'Отмена', tourTitle: 'Быстрый тур',
    tourSkip: 'Пропустить', tourNext: 'Далее', tourFinish: 'Готово'
  },
  ro: {
    ...translations.en,
    appTitle: 'Zile mondiale', favorites: 'Favorite', settings: 'Setări', home: 'Acasă', add: 'Adaugă', close: 'Închide',
    edit: 'Editează', share: 'Distribuie', category: 'Categorie', date: 'Data', language: 'Limbă', notifications: 'Notificări',
    notificationMode: 'Mod notificări', notificationTime: 'Oră', notificationDays: 'Zile de notificare', noNotification: 'Fără notificări',
    silentNotification: 'Fără sunet', soundNotification: 'Cu sunet', search: 'Caută sărbători', searchButton: 'Caută',
    allCountries: 'Toate țările', allCategories: 'Toate categoriile', save: 'Salvează', cancel: 'Anulează', tourTitle: 'Tur rapid',
    tourSkip: 'Omite', tourNext: 'Următorul', tourFinish: 'Finalizează'
  },
  ar: {
    ...translations.en,
    appTitle: 'الأيام العالمية', favorites: 'المفضلة', settings: 'الإعدادات', home: 'الرئيسية', add: 'إضافة', close: 'إغلاق',
    edit: 'تعديل', share: 'مشاركة', category: 'الفئة', date: 'التاريخ', language: 'اللغة', notifications: 'الإشعارات',
    notificationMode: 'وضع الإشعارات', notificationTime: 'الوقت', notificationDays: 'أيام الإشعارات', noNotification: 'بدون إشعارات',
    silentNotification: 'صامتة', soundNotification: 'مع صوت', search: 'البحث عن المناسبات', searchButton: 'بحث',
    allCountries: 'كل البلدان', allCategories: 'كل الفئات', save: 'حفظ', cancel: 'إلغاء', tourTitle: 'جولة سريعة',
    tourSkip: 'تخطي', tourNext: 'التالي', tourFinish: 'إنهاء'
  }
} as const;
const allTranslations = { ...translations, ...additionalTranslations };

export function resolveLanguage(language: Language): Exclude<Language, 'system'> {
  if (language !== 'system') return language;
  const browserLanguage = navigator.language.slice(0, 2) as Exclude<Language, 'system'>;
  return ['es', 'en', 'fr', 'zh', 'ja', 'pt', 'ca', 'ru', 'ro', 'ar'].includes(browserLanguage) ? browserLanguage : 'es';
}

export function t(language: Language, key: TranslationKey): string {
  const locale = allTranslations[resolveLanguage(language)] as Partial<Record<TranslationKey, string>>;
  return locale[key] ?? translations.es[key];
}

export function categoryName(language: Language, category: string): string {
  const names: Record<string, Record<string, string>> = {
    en: { health: 'Health', education: 'Education', environment: 'Environment', culture: 'Culture', science_tech: 'Science & technology' },
    fr: { health: 'Santé', education: 'Éducation', environment: 'Environnement', culture: 'Culture', science_tech: 'Science et technologie' },
    pt: { health: 'Saúde', education: 'Educação', environment: 'Meio ambiente', culture: 'Cultura', science_tech: 'Ciência e tecnologia' },
    ca: { health: 'Salut', education: 'Educació', environment: 'Medi ambient', culture: 'Cultura', science_tech: 'Ciència i tecnologia' },
    zh: { health: '健康', education: '教育', environment: '环境', culture: '文化', science_tech: '科学与技术' },
    ja: { health: '健康', education: '教育', environment: '環境', culture: '文化', science_tech: '科学技術' },
    ru: { health: 'Здоровье', education: 'Образование', environment: 'Окружающая среда', culture: 'Культура', science_tech: 'Наука и технологии' },
    ro: { health: 'Sănătate', education: 'Educație', environment: 'Mediu', culture: 'Cultură', science_tech: 'Știință și tehnologie' },
    ar: { health: 'الصحة', education: 'التعليم', environment: 'البيئة', culture: 'الثقافة', science_tech: 'العلوم والتكنولوجيا' },
    es: { health: 'Salud', education: 'Educación', environment: 'Medio ambiente', culture: 'Cultura', science_tech: 'Ciencia y tecnología' }
  };
  return names[resolveLanguage(language)][category.toLowerCase()] ?? category;
}
