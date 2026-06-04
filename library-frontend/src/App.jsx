import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import BookSearchPage from './pages/BookSearchPage.jsx';
import BookDetailsPage from './pages/BookDetailsPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import AdminPage from './pages/AdminPage.jsx';
import { libraryApi } from './api/libraryApi.js';

function App() {
  const userString = localStorage.getItem('user');
  const isAuthenticated = !!userString;
  
  const currentUser = isAuthenticated ? JSON.parse(userString) : null;
  const isLibrarian = currentUser?.role === 'LIBRARIAN';

  const handleLogout = async () => {
    try {
      await libraryApi.logoutUser();
    } catch (e) {
      console.error(e);
    }
    localStorage.removeItem('user');
    window.location.href = '/login';
  };

  return (
    <Router>
      <div style={{ fontFamily: 'Arial' }}>
        <nav style={{ backgroundColor: '#2c3e50', padding: '15px', display: 'flex', gap: '20px', alignItems: 'center' }}>
          <Link to="/" style={{ color: 'white', textDecoration: 'none', fontWeight: 'bold' }}>📚 Библиотека</Link>
          
          {!isAuthenticated ? (
            <>
              <Link to="/login" style={{ color: 'white', textDecoration: 'none' }}>Вход</Link>
              <Link to="/register" style={{ color: 'white', textDecoration: 'none' }}>Регистрация</Link>
            </>
          ) : (
            <>
              {isLibrarian && <Link to="/admin" style={{ color: 'white', textDecoration: 'none' }}>Управление (Админ)</Link>}
              
              <span style={{ color: '#bdc3c7', marginLeft: 'auto' }}>Привет, {currentUser.name} ({currentUser.role})</span>
              <button onClick={handleLogout} style={{ background: 'none', border: 'none', color: '#e74c3c', cursor: 'pointer' }}>Выйти</button>
            </>
          )}
        </nav>

        <main style={{ padding: '20px' }}>
          <Routes>
            <Route path="/" element={isAuthenticated ? <BookSearchPage /> : <Navigate to="/login" />} />
            <Route path="/books/:id" element={isAuthenticated ? <BookDetailsPage /> : <Navigate to="/login" />} />
            <Route path="/admin" element={isAuthenticated && isLibrarian ? <AdminPage /> : <Navigate to="/" />} />
            
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<LoginPage />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;