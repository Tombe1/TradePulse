import { useState, useEffect } from 'react'
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Typography,
  Box,
  Tabs,
  Tab,
  Chip,
} from '@mui/material'

export default function TradeModal({ asset, ownedQuantity, onClose, onTrade }) {
  const [quantity, setQuantity] = useState(1)
  const [tabValue, setTabValue] = useState(0) // 0 = Buy, 1 = Sell

  useEffect(() => {
    setQuantity(1)
  }, [asset])

  const isBuy = tabValue === 0
  const total = (asset.currentPrice * quantity).toFixed(2)

  // ולידציה: אי אפשר למכור יותר ממה שיש
  const isValid = isBuy ? true : ownedQuantity >= quantity && quantity > 0

  const handleSubmit = () => {
    const finalQty = isBuy ? quantity : -quantity // שלילי למכירה
    onTrade(asset.symbol, finalQty, asset.currentPrice)
  }

  return (
    <Dialog open={true} onClose={onClose} fullWidth maxWidth='xs' dir='rtl'>
      {/* התיקון: component="div" מונע את השגיאה בקונסול */}
      <DialogTitle component='div' sx={{ textAlign: 'center', pb: 1 }}>
        <Typography variant='h5' fontWeight='bold'>
          {asset.symbol}
        </Typography>
        <Typography variant='body2' color='textSecondary'>
          מחיר:{' '}
          <span style={{ direction: 'ltr', display: 'inline-block' }}>
            ${asset.currentPrice.toFixed(2)}
          </span>
        </Typography>
      </DialogTitle>

      <DialogContent>
        <Tabs
          value={tabValue}
          onChange={(e, val) => setTabValue(val)}
          variant='fullWidth'
          sx={{ mb: 3, borderBottom: 1, borderColor: 'divider' }}
        >
          <Tab
            label='קנייה'
            sx={{ color: 'success.main', fontWeight: 'bold' }}
          />
          <Tab label='מכירה' sx={{ color: 'error.main', fontWeight: 'bold' }} />
        </Tabs>

        {!isBuy && (
          <Box sx={{ textAlign: 'center', mb: 2 }}>
            <Chip
              label={`ברשותך: ${ownedQuantity} יחידות`}
              variant='outlined'
            />
          </Box>
        )}

        <TextField
          autoFocus
          label='כמות'
          type='number'
          fullWidth
          value={quantity}
          onChange={(e) =>
            setQuantity(Math.max(1, parseInt(e.target.value) || 0))
          }
          InputProps={{ inputProps: { min: 1 } }}
          sx={{ mb: 3 }}
        />

        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            p: 2,
            bgcolor: isBuy
              ? 'rgba(0, 230, 118, 0.1)'
              : 'rgba(255, 23, 68, 0.1)',
            borderRadius: 2,
          }}
        >
          <Typography variant='body1' fontWeight='bold'>
            סה"כ:
          </Typography>
          <Typography variant='h6' fontWeight='bold' sx={{ direction: 'ltr' }}>
            ${total}
          </Typography>
        </Box>
      </DialogContent>

      <DialogActions sx={{ p: 3, gap: 1 }}>
        <Button onClick={onClose} color='inherit'>
          ביטול
        </Button>
        <Button
          onClick={handleSubmit}
          variant='contained'
          color={isBuy ? 'success' : 'error'}
          disabled={!isValid}
          fullWidth
          sx={{ fontWeight: 'bold' }}
        >
          {isBuy ? 'בצע קנייה' : 'בצע מכירה'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
