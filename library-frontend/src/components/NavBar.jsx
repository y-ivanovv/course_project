import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/authContext.js';
import { colors } from '../styles/ui.js';

const linkStyle = { color: 'white', textDecoration: 'none' };

export default function NavBar() {
  const { isAuthenticated, isLibrarian, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav style={{
      backgroundColor: colors.dark, padding: 15,
      display: 'flex', gap: 20, alignItems: 'center',
    }}>
      <Link to="/" style={{ ...linkStyle, fontWeight: 'bold' }}>📚 Библиотека</Link>

      {!isAuthenticated ? (
        <>
          <Link to="/login" style={linkStyle}>Вход</Link>
          <Link to="/register" style={linkStyle}>Регистрация</Link>
        </>
      ) : (
        <>
          {isLibrarian && <Link to="/admin" style={linkStyle}>Управление (Админ)</Link>}
          <span style={{ color: '#bdc3c7', marginLeft: 'auto' }}>
            Привет, {user.name} ({user.role})
          </span>
          <button onClick={handleLogout} style={{ background: 'none', border: 'none', color: colors.danger, cursor: 'pointer' }}>
            Выйти
          </button>
        </>
      )}
    </nav>
  );
}
