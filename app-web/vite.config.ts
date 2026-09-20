import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    // Ionic bundles its component runtime as one shared dependency.
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        manualChunks: {
          ionic: ['@ionic/react'],
          capacitor: ['@capacitor/core', '@capacitor/app', '@capacitor/status-bar'],
          sqlite: ['@capacitor-community/sqlite'],
        },
      },
    },
  },
});
