import { useState } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true) // בורר בין לוגין להרשמה
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [email, setEmail] = useState('') // רלוונטי רק להרשמה
  const [error, setError] = useState('')

  const navigate = useNavigate() // הכלי שלנו למעבר עמודים

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    const endpoint = isLogin ? '/api/auth/login' : '/api/auth/register'
    const payload = isLogin
      ? { username, password }
      : { username, password, email }

    try {
      const res = await axios.post(endpoint, payload)

      if (isLogin) {
        // שומרים את הטוקן שקיבלנו מהג'אווה
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('username', username)
        alert('התחברת בהצלחה! 🚀')
        navigate('/dashboard') // מעבר לדשבורד (ניצור אותו בהמשך)
      } else {
        alert('נרשמת בהצלחה! עכשיו תתחבר.')
        setIsLogin(true) // מעבר למצב לוגין
      }
    } catch (err) {
      console.error(err)
      setError(err.response?.data?.message || 'שגיאה בהתחברות')
    }
  }

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>
          {isLogin ? 'כניסה למערכת' : 'הרשמה ל-TradePulse'}
        </h2>

        {error && <div style={styles.error}>{error}</div>}

        <form onSubmit={handleSubmit} style={styles.form}>
          <input
            style={styles.input}
            type='text'
            placeholder='שם משתמש'
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />

          {!isLogin && (
            <input
              style={styles.input}
              type='email'
              placeholder='אימייל'
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          )}

          <input
            style={styles.input}
            type='password'
            placeholder='סיסמה'
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type='submit' style={styles.button}>
            {isLogin ? 'התחבר' : 'הירשם'}
          </button>
        </form>

        <p style={styles.toggleText} onClick={() => setIsLogin(!isLogin)}>
          {isLogin ? 'אין לך חשבון? הירשם כאן' : 'יש לך חשבון? התחבר'}
        </p>
      </div>
    </div>
  )
}

// עיצוב (CSS-in-JS)
const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100vh',
    width: '100vw', // <--- השינוי החשוב: רוחב מלא
    background: '#f0f2f5',
    direction: 'rtl',
  },
  card: {
    background: 'white',
    padding: '40px',
    borderRadius: '12px',
    boxShadow: '0 4px 15px rgba(0,0,0,0.1)',
    width: '350px',
    textAlign: 'center',
  },
  title: { marginBottom: '20px', color: '#333' },
  form: { display: 'flex', flexDirection: 'column', gap: '15px' },
  input: {
    padding: '12px',
    borderRadius: '6px',
    border: '1px solid #ddd',
    fontSize: '16px',
  },
  button: {
    padding: '12px',
    background: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '16px',
    fontWeight: 'bold',
  },
  error: { color: 'red', marginBottom: '10px', fontSize: '14px' },
  toggleText: {
    marginTop: '15px',
    color: '#3498db',
    cursor: 'pointer',
    textDecoration: 'underline',
  },
}
