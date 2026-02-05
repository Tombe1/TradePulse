import { createTheme } from '@mui/material/styles'

const theme = createTheme({
  direction: 'rtl',
  palette: {
    mode: 'dark', // מצב כהה
    primary: {
      main: '#2196f3', // כחול ניאון
    },
    secondary: {
      main: '#f50057',
    },
    background: {
      default: '#0a1929', // כחול כהה מאוד (כמעט שחור)
      paper: '#132f4c', // צבע הכרטיסים
    },
    success: {
      main: '#00e676', // ירוק זרחני לרווחים
    },
    error: {
      main: '#ff1744', // אדום זרחני להפסדים
    },
    text: {
      primary: '#ffffff',
      secondary: '#b2bac2',
    },
  },
  typography: {
    fontFamily: 'Assistant, Roboto, sans-serif',
    h1: { fontWeight: 700 },
    h2: { fontWeight: 700 },
    h3: { fontWeight: 700 },
    h4: { fontWeight: 600 },
    h6: { fontWeight: 600 },
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage: 'none', // ביטול גרדיאנט דיפולטיבי של MUI
          backgroundColor: '#132f4c',
          borderRadius: '16px',
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.2)', // צל עמוק
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: '8px',
          textTransform: 'none',
          fontWeight: 'bold',
        },
      },
    },
  },
})

export default theme
