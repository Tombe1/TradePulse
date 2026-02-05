// src/services/api.js
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // --- שינוי: ביטלנו זמנית את הבעיטה החוצה ---
    if (error.response && error.response.status === 403) {
      console.error(
        'קיבלנו שגיאת 403 (אין הרשאה), אבל לא מתנתקים כרגע לצרכי בדיקה.',
      )
      // localStorage.removeItem('token'); // כרגע בהערה
      // window.location.href = '/';       // כרגע בהערה
    }
    return Promise.reject(error)
  },
)

export default api
