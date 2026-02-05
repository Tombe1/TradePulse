import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'
import {
  Button,
  TextField,
  Typography,
  Box,
  Link,
  CircularProgress,
} from '@mui/material'
import TrendingUpIcon from '@mui/icons-material/TrendingUp'

export default function LoginPage() {
  const [isLogin, setIsLogin] = useState(true)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)

  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)

    const endpoint = isLogin ? '/auth/login' : '/auth/register'
    const payload = isLogin
      ? { username, password }
      : { username, password, email }

    try {
      console.log('שולח בקשה ל:', endpoint)
      const res = await api.post(endpoint, payload)
      console.log('תשובת שרת גולמית:', res.data)

      if (isLogin) {
        // תיקון קריטי: תמיכה גם באובייקט וגם במחרוזת
        let token = null
        if (res.data && res.data.token) {
          token = res.data.token
        } else if (typeof res.data === 'string' && res.data.startsWith('ey')) {
          token = res.data
        }

        if (token) {
          console.log('✅ טוקן זוהה ונשמר')
          localStorage.setItem('token', token)
          localStorage.setItem('username', username)
          navigate('/dashboard') // מעבר לדשבורד
        } else {
          console.error('❌ לא נמצא טוקן בתשובה', res.data)
          alert('התחברות הצליחה, אך השרת לא החזיר מפתח גישה (Token).')
        }
      } else {
        alert('נרשמת בהצלחה! כעת התחבר.')
        setIsLogin(true)
      }
    } catch (err) {
      console.error('שגיאת התחברות:', err)
      const msg = err.response?.data?.message || 'שגיאה בתקשורת לשרת'
      alert(`שגיאה: ${msg}`)
    } finally {
      setLoading(false)
    }
  }

  // סגנון משותף לשדות כדי שיראו טוב ב-Dark Mode
  const textFieldStyles = {
    '& .MuiInputBase-input': { color: '#ffffff' }, // טקסט ההקלדה בלבן
    '& .MuiInputLabel-root': { color: '#b0bec5' }, // צבע הלייבל באפור בהיר
    '& .MuiInputLabel-root.Mui-focused': { color: '#2196f3' }, // לייבל בפוקוס
    '& .MuiOutlinedInput-root': {
      '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.23)' }, // מסגרת רגילה
      '&:hover fieldset': { borderColor: '#ffffff' }, // מסגרת בהובר
      '&.Mui-focused fieldset': { borderColor: '#2196f3' }, // מסגרת בפוקוס
    },
  }

  return (
    <Box
      sx={{
        display: 'flex',
        width: '100vw',
        height: '100vh',
        overflow: 'hidden',
        direction: 'ltr',
        bgcolor: '#0a1929', // צבע רקע כהה ידני ליתר ביטחון
      }}
    >
      {/* צד שמאל: החלק האמנותי */}
      <Box
        sx={{
          flex: 1.5,
          background:
            'radial-gradient(circle at 10% 20%, #0f2027 0%, #203a43 60%, #2c5364 100%)',
          display: { xs: 'none', md: 'flex' },
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          color: 'white',
          position: 'relative',
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            top: '10%',
            left: '10%',
            width: 300,
            height: 300,
            borderRadius: '50%',
            background: 'rgba(255,255,255,0.03)',
            filter: 'blur(50px)',
          }}
        />

        <Box sx={{ zIndex: 2, textAlign: 'center', p: 4, direction: 'rtl' }}>
          <Typography
            variant='h2'
            fontWeight='800'
            sx={{ mb: 2, textShadow: '0 10px 30px rgba(0,0,0,0.5)' }}
          >
            לסחור חכם יותר.
          </Typography>
          <Typography variant='h5' sx={{ opacity: 0.8, fontWeight: 300 }}>
            מערכת המסחר המתקדמת שמביאה את השוק אליך הביתה.
          </Typography>
        </Box>
      </Box>

      {/* צד ימין: הטופס */}
      <Box
        sx={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          bgcolor: '#132f4c', // רקע כהה לכרטיס
          boxShadow: '-10px 0 30px rgba(0,0,0,0.2)',
          position: 'relative',
          zIndex: 10,
        }}
      >
        <Box
          sx={{
            width: '100%',
            maxWidth: 450,
            p: 6,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            direction: 'rtl',
          }}
        >
          <Box sx={{ mb: 4, display: 'flex', alignItems: 'center' }}>
            <TrendingUpIcon sx={{ fontSize: 40, color: '#2196f3', ml: 1 }} />
            <Typography
              variant='h4'
              color='#2196f3'
              fontWeight='900'
              sx={{ letterSpacing: 1 }}
            >
              TradePulse
            </Typography>
          </Box>

          <Typography
            variant='h5'
            sx={{ mb: 1, fontWeight: 'bold', color: '#ffffff' }}
          >
            {isLogin ? 'ברוכים השבים 👋' : 'הרשמה למערכת'}
          </Typography>
          <Typography variant='body1' sx={{ mb: 4, color: '#b0bec5' }}>
            {isLogin ? 'הזן את פרטיך לכניסה' : 'מלא את הפרטים כדי להתחיל'}
          </Typography>

          <Box component='form' onSubmit={handleSubmit} sx={{ width: '100%' }}>
            <TextField
              margin='normal'
              required
              fullWidth
              label='שם משתמש'
              autoFocus
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              sx={textFieldStyles}
            />

            {!isLogin && (
              <TextField
                margin='normal'
                required
                fullWidth
                label='כתובת אימייל'
                type='email'
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                sx={textFieldStyles}
              />
            )}

            <TextField
              margin='normal'
              required
              fullWidth
              label='סיסמה'
              type='password'
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              sx={textFieldStyles}
            />

            <Button
              type='submit'
              fullWidth
              variant='contained'
              disabled={loading}
              sx={{
                mt: 4,
                mb: 3,
                height: 50,
                fontSize: '1.1rem',
                borderRadius: 2,
                boxShadow: '0 4px 14px rgba(33, 150, 243, 0.3)',
                background: 'linear-gradient(45deg, #2196f3 30%, #21CBF3 90%)',
                color: 'white',
                fontWeight: 'bold',
              }}
            >
              {loading ? (
                <CircularProgress size={24} color='inherit' />
              ) : isLogin ? (
                'כניסה למערכת'
              ) : (
                'הרשמה חינם'
              )}
            </Button>

            <Box sx={{ textAlign: 'center' }}>
              <Link
                component='button'
                variant='body1'
                onClick={(e) => {
                  e.preventDefault()
                  setIsLogin(!isLogin)
                }}
                sx={{
                  textDecoration: 'none',
                  fontWeight: 600,
                  color: '#2196f3',
                }}
              >
                {isLogin ? 'אין לך חשבון? הירשם עכשיו' : 'כבר רשום? התחבר'}
              </Link>
            </Box>
          </Box>
        </Box>
      </Box>
    </Box>
  )
}
