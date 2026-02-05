import { useEffect, useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import SockJS from 'sockjs-client/dist/sockjs'
import Stomp from 'stompjs'
import api from '../services/api'
import Navbar from '../components/Navbar'
import TradeModal from '../components/TradeModal'

// תיקון קריטי: ייבוא Grid רגיל (כי Grid2 לא קיים בגרסה שלך כייבוא נפרד)
import {
  Container,
  Grid,
  Typography,
  Box,
  Card,
  CardContent,
  CircularProgress,
  Button,
  Chip,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableRow,
  Avatar,
  Divider,
} from '@mui/material'

import TrendingUpIcon from '@mui/icons-material/TrendingUp'
import TrendingDownIcon from '@mui/icons-material/TrendingDown'
import WalletIcon from '@mui/icons-material/AccountBalanceWallet'
import HistoryIcon from '@mui/icons-material/History'
import AutoGraphIcon from '@mui/icons-material/AutoGraph'
import ShowChartIcon from '@mui/icons-material/ShowChart'

export default function DashboardPage() {
  const [portfolio, setPortfolio] = useState([])
  const [assets, setAssets] = useState([])
  const [transactions, setTransactions] = useState([])
  const [username, setUsername] = useState('')
  const [selectedAsset, setSelectedAsset] = useState(null)
  const [loading, setLoading] = useState(true)

  const stompClientRef = useRef(null)
  const navigate = useNavigate()

  const fetchData = async () => {
    try {
      const [portfolioRes, assetsRes] = await Promise.all([
        api.get('/users/portfolio'),
        api.get('/assets'),
      ])

      // תיקון פונקציונאלי: המרה למספרים וסינון מניות ריקות
      const cleanPortfolio = portfolioRes.data
        .map((item) => ({ ...item, quantity: Number(item.quantity) }))
        .filter((item) => item.quantity > 0.001) // מסנן הכל חוץ ממה שחיובי

      setPortfolio(cleanPortfolio)
      setAssets(assetsRes.data)

      try {
        const historyRes = await api.get('/users/transactions')
        setTransactions(historyRes.data)
      } catch (e) {
        setTransactions([])
      }
    } catch (err) {
      console.error('Failed to fetch data', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    const user = localStorage.getItem('username')
    if (!user) navigate('/')
    setUsername(user)
    fetchData()

    const socket = new SockJS('http://localhost:8080/ws')
    const client = Stomp.over(socket)
    client.debug = () => {}

    client.connect(
      {},
      () => {
        client.subscribe('/topic/prices', (message) => {
          if (message.body) {
            const update = JSON.parse(message.body)
            setAssets((prev) =>
              prev.map((a) =>
                a.symbol === update.symbol
                  ? { ...a, currentPrice: update.currentPrice }
                  : a,
              ),
            )
          }
        })
      },
      () => {},
    )

    stompClientRef.current = client
    return () => {
      if (stompClientRef.current?.connected) stompClientRef.current.disconnect()
    }
  }, [navigate])

  const executeTrade = async (symbol, qty, price) => {
    try {
      // תיקון פונקציונאלי: מחיקה מיידית מהמסך (לפני שהשרת עונה)
      setPortfolio((prev) => {
        const existing = prev.find((p) => p.asset.symbol === symbol)
        if (existing) {
          const newQty = Number(existing.quantity) + Number(qty)

          // אם הכמות התאפסה (או ירדה מתחת ל-0) -> מעיפים מהמערך מיד!
          if (newQty <= 0.001) {
            return prev.filter((p) => p.asset.symbol !== symbol)
          }
          // אחרת מעדכנים
          return prev.map((p) =>
            p.asset.symbol === symbol ? { ...p, quantity: newQty } : p,
          )
        }
        return prev
      })

      // שליחה לשרת
      await api.post(`/users/trade?symbol=${symbol}&qty=${qty}&price=${price}`)

      setSelectedAsset(null)
      // רענון מושהה כדי לוודא שהדאטהבייס התעדכן לפני שמושכים שוב
      setTimeout(fetchData, 1000)
    } catch (err) {
      alert('שגיאה: ' + (err.response?.data?.message || err.message))
      fetchData() // שחזור במקרה של כישלון
    }
  }

  // חישובים
  const activePortfolio = portfolio
  const totalPortfolioValue = activePortfolio.reduce((sum, item) => {
    const currentPrice =
      assets.find((a) => a.symbol === item.asset.symbol)?.currentPrice || 0
    return sum + item.quantity * currentPrice
  }, 0)
  const totalInvested = activePortfolio.reduce(
    (sum, item) => sum + item.quantity * item.averageBuyPrice,
    0,
  )
  const totalPnL = totalPortfolioValue - totalInvested
  const totalPnLPercent =
    totalInvested > 0 ? (totalPnL / totalInvested) * 100 : 0
  const dailyPnL = activePortfolio.reduce((sum, item) => {
    const asset =
      assets.find((a) => a.symbol === item.asset.symbol) || item.asset
    const prev = asset.previousClosePrice || asset.currentPrice
    return sum + (asset.currentPrice - prev) * item.quantity
  }, 0)

  const getColor = (val) => (val >= 0 ? '#00e676' : '#ff1744')

  if (loading)
    return (
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'center',
          height: '100vh',
          alignItems: 'center',
          bgcolor: '#0b0e11',
        }}
      >
        <CircularProgress />
      </Box>
    )

  return (
    <Box
      sx={{
        minHeight: '100vh',
        pb: 4,
        bgcolor: '#0b0e11',
        color: 'white',
        direction: 'rtl',
      }}
    >
      <Navbar username={username} />

      <Container maxWidth='xl' sx={{ mt: 4 }}>
        {/* תיקון האזהרות הצהובות: 
                   במקום 'item' ו-'xs', אנחנו משתמשים ב-'size' שמתאים לגרסה החדשה של Grid שרצה אצלך.
                */}
        <Grid container spacing={2} sx={{ mb: 4 }}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card
              sx={{
                background: 'linear-gradient(145deg, #1e2329 0%, #161a1e 100%)',
                borderRadius: 3,
                border: '1px solid #2b3139',
              }}
            >
              <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <Box>
                    <Typography
                      color='#848e9c'
                      variant='caption'
                      fontWeight='bold'
                    >
                      שווי תיק כולל
                    </Typography>
                    <Typography
                      variant='h5'
                      fontWeight='bold'
                      sx={{ mt: 0.5, direction: 'ltr' }}
                    >
                      $
                      {totalPortfolioValue.toLocaleString(undefined, {
                        minimumFractionDigits: 2,
                      })}
                    </Typography>
                  </Box>
                  <Avatar
                    sx={{
                      bgcolor: 'rgba(33, 150, 243, 0.1)',
                      color: '#2196f3',
                      width: 40,
                      height: 40,
                    }}
                  >
                    <WalletIcon />
                  </Avatar>
                </Box>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card
              sx={{
                background: '#1e2329',
                borderRadius: 3,
                borderRight: `4px solid ${getColor(totalPnL)}`,
              }}
            >
              <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
                <Typography color='#848e9c' variant='caption' fontWeight='bold'>
                  רווח/הפסד (PNL)
                </Typography>
                <Box
                  sx={{
                    display: 'flex',
                    gap: 1,
                    alignItems: 'center',
                    mt: 0.5,
                    direction: 'ltr',
                  }}
                >
                  <Typography
                    variant='h5'
                    fontWeight='bold'
                    sx={{ color: getColor(totalPnL) }}
                  >
                    {totalPnL > 0 ? '+' : ''}
                    {totalPnL.toFixed(2)}$
                  </Typography>
                  <Chip
                    label={`${totalPnLPercent.toFixed(2)}%`}
                    size='small'
                    sx={{
                      bgcolor:
                        totalPnL >= 0
                          ? 'rgba(0,230,118,0.1)'
                          : 'rgba(255,23,68,0.1)',
                      color: getColor(totalPnL),
                      fontWeight: 'bold',
                      height: 20,
                      fontSize: '0.75rem',
                    }}
                  />
                </Box>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card
              sx={{
                background: '#1e2329',
                borderRadius: 3,
                borderRight: `4px solid ${getColor(dailyPnL)}`,
              }}
            >
              <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
                <Typography color='#848e9c' variant='caption' fontWeight='bold'>
                  שינוי יומי
                </Typography>
                <Box
                  sx={{
                    display: 'flex',
                    gap: 1,
                    alignItems: 'center',
                    mt: 0.5,
                    direction: 'ltr',
                  }}
                >
                  <Typography
                    variant='h5'
                    fontWeight='bold'
                    sx={{ color: getColor(dailyPnL) }}
                  >
                    {dailyPnL > 0 ? '+' : ''}
                    {dailyPnL.toFixed(2)}$
                  </Typography>
                  {dailyPnL !== 0 &&
                    (dailyPnL > 0 ? (
                      <TrendingUpIcon
                        fontSize='small'
                        sx={{ color: getColor(dailyPnL) }}
                      />
                    ) : (
                      <TrendingDownIcon
                        fontSize='small'
                        sx={{ color: getColor(dailyPnL) }}
                      />
                    ))}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        <Grid container spacing={3}>
          {/* צד ימין: שוק והיסטוריה */}
          <Grid size={{ xs: 12, lg: 3 }}>
            <Box sx={{ mb: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <ShowChartIcon sx={{ ml: 1, color: '#f50057', fontSize: 20 }} />
                <Typography variant='subtitle1' fontWeight='bold'>
                  השוק עכשיו
                </Typography>
              </Box>

              <Paper
                sx={{
                  height: 450,
                  overflow: 'auto',
                  bgcolor: '#1e2329',
                  borderRadius: 3,
                  '&::-webkit-scrollbar': { width: '6px' },
                  '&::-webkit-scrollbar-track': { background: '#161a1e' },
                  '&::-webkit-scrollbar-thumb': {
                    background: '#2b3139',
                    borderRadius: '3px',
                  },
                }}
              >
                {assets.map((asset) => (
                  <Box
                    key={asset.symbol}
                    onClick={() => setSelectedAsset(asset)}
                    sx={{
                      p: 1.5,
                      borderBottom: '1px solid #2b3139',
                      cursor: 'pointer',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      transition: '0.2s',
                      '&:hover': { bgcolor: '#2b3139', pl: 2 },
                    }}
                  >
                    <Box>
                      <Typography
                        variant='body2'
                        fontWeight='bold'
                        sx={{ color: 'white' }}
                      >
                        {asset.symbol}
                      </Typography>
                      <Typography
                        variant='caption'
                        sx={{ color: '#848e9c', fontSize: '0.7rem' }}
                      >
                        Vol: 2.4M
                      </Typography>
                    </Box>
                    <Box sx={{ textAlign: 'left' }}>
                      <Typography
                        variant='body2'
                        fontWeight='bold'
                        sx={{ direction: 'ltr' }}
                      >
                        ${asset.currentPrice.toFixed(2)}
                      </Typography>
                      <Typography
                        variant='caption'
                        sx={{
                          color: getColor(
                            asset.currentPrice -
                              (asset.previousClosePrice || asset.currentPrice),
                          ),
                          direction: 'ltr',
                          display: 'block',
                          fontSize: '0.7rem',
                        }}
                      >
                        {(
                          ((asset.currentPrice -
                            (asset.previousClosePrice || asset.currentPrice)) /
                            (asset.previousClosePrice || 1)) *
                          100
                        ).toFixed(2)}
                        %
                      </Typography>
                    </Box>
                  </Box>
                ))}
              </Paper>
            </Box>

            <Box>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <HistoryIcon sx={{ ml: 1, color: '#848e9c', fontSize: 20 }} />
                <Typography variant='subtitle1' fontWeight='bold'>
                  פעולות אחרונות
                </Typography>
              </Box>
              <Paper
                sx={{ bgcolor: '#1e2329', borderRadius: 3, overflow: 'hidden' }}
              >
                {transactions.length === 0 ? (
                  <Box sx={{ p: 2, textAlign: 'center' }}>
                    <Typography variant='caption' color='#848e9c'>
                      אין נתונים
                    </Typography>
                  </Box>
                ) : (
                  <Table size='small'>
                    <TableBody>
                      {transactions.slice(0, 5).map((tx, idx) => (
                        <TableRow
                          key={idx}
                          sx={{
                            '& td': {
                              borderBottom: '1px solid #2b3139',
                              py: 1,
                            },
                            '&:last-child td': { border: 0 },
                          }}
                        >
                          <TableCell
                            sx={{ color: 'white', fontSize: '0.8rem' }}
                          >
                            {tx.symbol}
                          </TableCell>
                          <TableCell align='center'>
                            <span
                              style={{
                                color:
                                  tx.type === 'BUY' ? '#00e676' : '#ff1744',
                                fontSize: '0.75rem',
                                fontWeight: 'bold',
                              }}
                            >
                              {tx.type === 'BUY' ? 'קניה' : 'מכירה'}
                            </span>
                          </TableCell>
                          <TableCell
                            sx={{
                              color: '#848e9c',
                              direction: 'ltr',
                              fontSize: '0.8rem',
                            }}
                            align='left'
                          >
                            {tx.price.toFixed(2)}$
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                )}
              </Paper>
            </Box>
          </Grid>

          {/* צד שמאל: התיק שלי */}
          <Grid size={{ xs: 12, lg: 9 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
              <AutoGraphIcon sx={{ ml: 1, color: '#fcd535' }} />
              <Typography variant='h5' fontWeight='bold'>
                הנכסים שלי
              </Typography>
            </Box>

            {activePortfolio.length === 0 ? (
              <Paper
                sx={{
                  p: 8,
                  textAlign: 'center',
                  bgcolor: '#1e2329',
                  borderRadius: 4,
                  border: '1px dashed #474d57',
                }}
              >
                <Typography variant='h6' color='#848e9c' sx={{ mb: 2 }}>
                  התיק שלך ריק כרגע
                </Typography>
                <Typography variant='body2' color='#5e6673'>
                  בחר מניה מרשימת השוק בצד ימין כדי להתחיל לסחור 🚀
                </Typography>
              </Paper>
            ) : (
              <Grid container spacing={2}>
                {activePortfolio.map((item) => {
                  const asset =
                    assets.find((a) => a.symbol === item.asset.symbol) ||
                    item.asset
                  const profit =
                    (asset.currentPrice - item.averageBuyPrice) * item.quantity
                  const profitP = item.averageBuyPrice
                    ? ((asset.currentPrice - item.averageBuyPrice) /
                        item.averageBuyPrice) *
                      100
                    : 0

                  return (
                    <Grid size={{ xs: 12, sm: 6, md: 4 }} key={item.id}>
                      <Card
                        sx={{
                          bgcolor: '#1e2329',
                          color: 'white',
                          borderRadius: 3,
                          border: '1px solid #2b3139',
                          transition: '0.2s',
                          '&:hover': {
                            transform: 'translateY(-4px)',
                            borderColor: '#474d57',
                            boxShadow: '0 4px 20px rgba(0,0,0,0.3)',
                          },
                        }}
                      >
                        <CardContent>
                          <Box
                            sx={{
                              display: 'flex',
                              justifyContent: 'space-between',
                              mb: 2,
                            }}
                          >
                            <Typography
                              variant='h6'
                              fontWeight='bold'
                              sx={{ letterSpacing: 1 }}
                            >
                              {asset.symbol}
                            </Typography>
                            <Chip
                              label={`${item.quantity} יח'`}
                              size='small'
                              sx={{
                                bgcolor: '#2b3139',
                                color: '#b7bdc6',
                                height: 24,
                              }}
                            />
                          </Box>

                          <Typography
                            variant='h4'
                            fontWeight='bold'
                            sx={{ direction: 'ltr', mb: 2 }}
                          >
                            ${asset.currentPrice.toFixed(2)}
                          </Typography>

                          <Divider sx={{ bgcolor: '#2b3139', mb: 2 }} />

                          <Box
                            sx={{
                              display: 'flex',
                              justifyContent: 'space-between',
                              alignItems: 'center',
                            }}
                          >
                            <Typography variant='caption' color='#848e9c'>
                              רווח/הפסד
                            </Typography>
                            <Box sx={{ textAlign: 'left' }}>
                              <Typography
                                variant='body1'
                                sx={{
                                  color: getColor(profit),
                                  fontWeight: 'bold',
                                  direction: 'ltr',
                                  lineHeight: 1,
                                }}
                              >
                                {profit > 0 ? '+' : ''}
                                {profit.toFixed(2)}$
                              </Typography>
                              <Typography
                                variant='caption'
                                sx={{
                                  color: getColor(profit),
                                  direction: 'ltr',
                                  display: 'block',
                                  textAlign: 'right',
                                }}
                              >
                                {profitP.toFixed(2)}%
                              </Typography>
                            </Box>
                          </Box>

                          <Button
                            fullWidth
                            variant='contained'
                            size='small'
                            sx={{
                              mt: 3,
                              bgcolor: '#2b3139',
                              color: 'white',
                              '&:hover': { bgcolor: '#2196f3' },
                            }}
                            onClick={() => setSelectedAsset(asset)}
                          >
                            פעולות
                          </Button>
                        </CardContent>
                      </Card>
                    </Grid>
                  )
                })}
              </Grid>
            )}
          </Grid>
        </Grid>
      </Container>

      {selectedAsset && (
        <TradeModal
          asset={selectedAsset}
          ownedQuantity={
            portfolio.find((p) => p.asset.symbol === selectedAsset.symbol)
              ?.quantity || 0
          }
          onClose={() => setSelectedAsset(null)}
          onTrade={executeTrade}
        />
      )}
    </Box>
  )
}
