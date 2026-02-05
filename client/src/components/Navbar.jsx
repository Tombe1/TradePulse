import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  Avatar,
  Container,
} from '@mui/material'
import TrendingUpIcon from '@mui/icons-material/TrendingUp'
import LogoutIcon from '@mui/icons-material/Logout'
import { useNavigate } from 'react-router-dom'

export default function Navbar({ username }) {
  const navigate = useNavigate()

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    navigate('/')
  }

  return (
    <AppBar
      position='static'
      elevation={0}
      sx={{
        background: 'transparent',
        borderBottom: '1px solid rgba(255,255,255,0.1)',
      }}
    >
      <Container maxWidth='xl'>
        <Toolbar disableGutters>
          {/* לוגו */}
          <TrendingUpIcon sx={{ color: '#2196f3', mr: 1, fontSize: 30 }} />
          <Typography
            variant='h5'
            component='div'
            sx={{
              flexGrow: 1,
              fontWeight: '900',
              letterSpacing: '1px',
              background: 'linear-gradient(45deg, #2196f3 30%, #21CBF3 90%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            TradePulse{' '}
            <span
              style={{
                fontSize: '0.6em',
                color: '#fff',
                WebkitTextFillColor: '#fff',
              }}
            >
              PRO
            </span>
          </Typography>

          {/* משתמש */}
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Box
              sx={{ textAlign: 'right', display: { xs: 'none', sm: 'block' } }}
            >
              <Typography
                variant='body2'
                color='textSecondary'
                sx={{ fontSize: '0.8rem' }}
              >
                מחובר כ-
              </Typography>
              <Typography variant='subtitle2' sx={{ fontWeight: 'bold' }}>
                {username}
              </Typography>
            </Box>
            <Avatar
              sx={{
                bgcolor: 'rgba(33, 150, 243, 0.2)',
                color: '#2196f3',
                border: '1px solid #2196f3',
              }}
            >
              {username?.charAt(0).toUpperCase()}
            </Avatar>

            <Button
              color='error'
              variant='text'
              size='small'
              startIcon={<LogoutIcon />}
              onClick={handleLogout}
            >
              יציאה
            </Button>
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  )
}
