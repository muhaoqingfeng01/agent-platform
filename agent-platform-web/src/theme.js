import { theme } from 'antd'

const shared = {
  borderRadius: 10,
  fontFamily: "'IBM Plex Sans', 'Segoe UI', sans-serif",
  colorPrimary: '#3d8bfd',
}

export function getAntdTheme(mode) {
  const dark = mode !== 'light'
  return {
    algorithm: dark ? theme.darkAlgorithm : theme.defaultAlgorithm,
    token: {
      ...shared,
      colorPrimary: dark ? '#3d8bfd' : '#2563eb',
      colorBgBase: dark ? '#0f1115' : '#f4f6f9',
      colorBgContainer: dark ? '#141821' : '#ffffff',
      colorBorder: dark ? '#2a2f3a' : '#d7dee8',
      colorText: dark ? '#e8eaed' : '#1a2332',
    },
    components: {
      Layout: {
        headerBg: dark ? '#141821' : '#ffffff',
        siderBg: dark ? '#141821' : '#ffffff',
        bodyBg: 'transparent',
      },
      Button: {
        primaryShadow: 'none',
      },
    },
  }
}
