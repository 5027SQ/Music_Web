import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/user': 'http://localhost:8080',
      '/song': 'http://localhost:8080',
      '/artist': 'http://localhost:8080',
      '/test': 'http://localhost:8080',
      '/admin': 'http://localhost:8080'
    }
  }
});