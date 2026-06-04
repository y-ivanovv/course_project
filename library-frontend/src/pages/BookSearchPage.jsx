import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';
import { useBooks } from '../hooks/useBooks.js';
import { ui, button, statusBadge, colors } from '../styles/ui.js';

const BookSearchPage = () => {
  const { books, setBooks, reload } = useBooks();
  const [query, setQuery] = useState('');
  const navigate = useNavigate();

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query.trim()) {
      reload();
      return;
    }
    try {
      const response = await libraryApi.searchBooks(query);
      setBooks(response.data);
    } catch {
      // ошибку поиска показывать не критично — оставляем текущий список
    }
  };

  return (
    <div style={{ maxWidth: 800, margin: '30px auto', ...ui.page }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Полнотекстовый поиск книг (Elasticsearch)</h2>
        <button onClick={() => navigate('/admin')} style={button(colors.dark)}>Панель библиотекаря ↗</button>
      </div>

      <form onSubmit={handleSearch} style={{ display: 'flex', gap: 10, margin: '20px 0' }}>
        <input
          type="text"
          placeholder="Введите название или описание книги (например: руководству)..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          style={{ ...ui.input, flex: 1 }}
        />
        <button type="submit" style={button()}>Искать</button>
      </form>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 15 }}>
        {books.map((book) => (
          <div key={book.id} style={ui.card}>
            <h3>{book.title}</h3>
            <p><strong>Автор:</strong> {book.author}</p>
            <p style={{ color: colors.muted, fontSize: 14 }}>{book.description?.substring(0, 80)}...</p>
            <span style={statusBadge(book.status)}>{book.status}</span>
            <button
              onClick={() => navigate(`/books/${book.id}`)}
              style={{ ...button('#f1f1f1'), color: '#000', display: 'block', marginTop: 10 }}
            >
              Подробнее
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default BookSearchPage;
