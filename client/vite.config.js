import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // הכתובת של הג'אווה שלך
        changeOrigin: true,
        secure: false,
      },
      '/ws': {
        // גם את ה-WebSocket נעביר דרך כאן
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true,
      },
    },
  },
})
