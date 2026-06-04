import { createContext, useContext } from 'react';

// Контекст и хук вынесены в отдельный (не компонентный) файл,
// чтобы не нарушать правило react-refresh/only-export-components.
export const AuthContext = createContext(null);

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth должен использоваться внутри <AuthProvider>');
  }
  return context;
}
