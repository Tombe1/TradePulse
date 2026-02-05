import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import { ThemeProvider } from '@mui/material/styles'
import CssBaseline from '@mui/material/CssBaseline'
import theme from './theme/theme'
// כאן נמחק את ה-import './index.css' הישן

// התקנת הפונט (אופציונלי, יוסיף מראה יפה)
import '@fontsource/roboto/300.css'
import '@fontsource/roboto/400.css'
import '@fontsource/roboto/500.css'
import '@fontsource/roboto/700.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* ספק העיצוב - מזרים את הצבעים לכל האפליקציה */}
    <ThemeProvider theme={theme}>
      {/* CssBaseline - מאפס את הדפדפן (כמו שעשינו ידני קודם) */}
      <CssBaseline />
      <App />
    </ThemeProvider>
  </React.StrictMode>,
)
