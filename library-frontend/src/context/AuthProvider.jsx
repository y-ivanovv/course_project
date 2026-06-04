import { useState, useCallback, useMemo } from 'react';
import { libraryApi } from '../api/libraryApi';
import { AuthContext } from './authContext.js';

const STORAGE_KEY = 'user';

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    return saved ? JSON.parse(saved) : null;
  });

  const login = useCallback((userData) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(userData));
    setUser(userData);
  }, []);

  const logout = useCallback(async () => {
    try {
      await libraryApi.logoutUser();
    } catch {
      // выход на сервере не критичен — всё равно очищаем клиента
    }
    localStorage.removeItem(STORAGE_KEY);
    setUser(null);
  }, []);

  const value = useMemo(() => ({
    user,
    isAuthenticated: !!user,
    isLibrarian: user?.role === 'LIBRARIAN',
    login,
    logout,
  }), [user, login, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
