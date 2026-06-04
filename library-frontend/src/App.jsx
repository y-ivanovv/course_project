import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import NavBar from './components/NavBar.jsx';
import { useAuth } from './context/authContext.js';
import BookSearchPage from './pages/BookSearchPage.jsx';
import BookDetailsPage from './pages/BookDetailsPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import AdminPage from './pages/AdminPage.jsx';

function App() {
  const { isAuthenticated, isLibrarian } = useAuth();

  return (
    <Router>
      <div style={{ fontFamily: 'Arial, sans-serif' }}>
        <NavBar />
        <main style={{ padding: 20 }}>
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
