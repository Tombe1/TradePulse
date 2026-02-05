import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage' // וודא שהקובץ קיים בתיקיית pages

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* דף הבית (לוגין) */}
        <Route path='/' element={<LoginPage />} />

        {/* דף הדשבורד (מוגן) */}
        <Route path='/dashboard' element={<DashboardPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
