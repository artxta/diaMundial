import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.jose.diamundial',
  appName: 'Día Mundial',
  webDir: 'dist',
  plugins: { CapacitorSQLite: { iosDatabaseLocation: 'Library/CapacitorDatabase' } }
};

export default config;
