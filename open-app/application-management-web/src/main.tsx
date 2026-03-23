import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'

// 开发环境下启用 MSW Mock
if (import.meta.env.DEV && import.meta.env.VITE_MOCK_ENABLED === 'true') {
  const { worker } = await import('./mocks/browser')
  worker.start({
    onUnhandledRequest: 'bypass',
  })
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
