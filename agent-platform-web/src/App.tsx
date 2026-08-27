import { useEffect } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { ConfigProvider, App as AntApp } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { AuthGuard } from '@/components/AuthGuard';
import { ChatPage } from '@/pages/Chat/ChatPage';
import { LoginPage } from '@/pages/Login/LoginPage';
import { useAuthStore } from '@/stores/useAuthStore';

export function App() {
  const hydrate = useAuthStore((s) => s.hydrate);
  const token = useAuthStore((s) => s.token);

  useEffect(() => {
    void hydrate();
  }, [hydrate]);

  return (
    <ConfigProvider locale={zhCN} theme={{ token: { colorPrimary: '#1677ff' } }}>
      <AntApp>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={token ? <Navigate to="/chat" replace /> : <LoginPage />} />
            <Route element={<AuthGuard />}>
              <Route path="/chat" element={<ChatPage />} />
            </Route>
            <Route path="*" element={<Navigate to={token ? '/chat' : '/login'} replace />} />
          </Routes>
        </BrowserRouter>
      </AntApp>
    </ConfigProvider>
  );
}
