# Día Mundial

Migración React/Ionic/Capacitor de la aplicación Android de referencia
`../dia_mundial_old`.

## Desarrollo

```bash
pnpm install
pnpm run dev
pnpm run build
```

Los datos de las siete traducciones se conservaron desde la aplicación antigua
en `public/data`. La interfaz carga esos datos sin red. Las celebraciones
personalizadas se guardan en SQLite mediante `@capacitor-community/sqlite` en
Android; en navegador se usa `localStorage` únicamente como fallback para
desarrollo web.

## Android

```bash
pnpm exec cap sync android
cd android
./gradlew assembleDebug
```

La compilación Android requiere un Android SDK configurado mediante
`ANDROID_HOME` o `android/local.properties`. El esquema SQLite se crea de
forma idempotente al abrir la aplicación, por lo que no se elimina la base de
datos al actualizar.
