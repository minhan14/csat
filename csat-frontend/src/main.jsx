import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
// This is the line that was missing.
// It tells Vite to load our CSS file, which activates Tailwind.
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)

