import React, { useEffect } from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import App from './App.jsx'
import { getAntdTheme } from './theme.js'
import { useThemeStore } from './store.js'
import './styles/tokens.css'

function Root() {
  const themeMode = useThemeStore((s) => s.theme)
  const applyDomTheme = useThemeStore((s) => s.applyDomTheme)

  useEffect(() => {
    applyDomTheme()
  }, [themeMode, applyDomTheme])

  return (
    <BrowserRouter>
      <ConfigProvider locale={zhCN} theme={getAntdTheme(themeMode)}>
        <App />
      </ConfigProvider>
    </BrowserRouter>
  )
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Root />
  </React.StrictMode>,
)
