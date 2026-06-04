import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';
import { useAuth } from '../context/authContext.js';
import { ui, button, statusBadge, colors } from '../styles/ui.js';

const PAGE_SIZE = 12;

const BookSearchPage = () => {
  const { isLibrarian } = useAuth();
  const navigate = useNavigate();

  const [books, setBooks] = useState([]);
  const [total, setTotal] = useState(0);
  const [offset, setOffset] = useState(0);
  const [query, setQuery] = useState('');        // текст в поле ввода
  const [activeQuery, setActiveQuery] = useState(''); // отправленный запрос (для пагинации)

  // Загрузка страницы: список или результаты поиска, в зависимости от activeQuery.
  const loadPage = useCallback(async (newOffset, q) => {
    try {
      const response = q
        ? await libraryApi.searchBooks(q, newOffset, PAGE_SIZE)
        : await libraryApi.getAllBooks(newOffset, PAGE_SIZE);
      setBooks(response.data.content);
      setTotal(response.data.total);
      setOffset(newOffset);
    } catch {
      // ошибку загрузки не показываем — оставляем предыдущее состояние
    }
  }, []);

  // Первичная загрузка (setState только из асинхронного колбэка)
  useEffect(() => {
    let active = true;
    libraryApi.getAllBooks(0, PAGE_SIZE)
      .then((response) => {
        if (active) {
          setBooks(response.data.content);
          setTotal(response.data.total);
        }
      })
      .catch(() => {});
    return () => { active = false; };
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    const q = query.trim();
    setActiveQuery(q);
    loadPage(0, q);
  };

  const totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
  const currentPage = Math.floor(offset / PAGE_SIZE) + 1;
  const canPrev = offset > 0;
  const canNext = offset + PAGE_SIZE < total;

  return (
    <div style={{ maxWidth: 800, margin: '30px auto', ...ui.page }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Полнотекстовый поиск книг (Elasticsearch)</h2>
        {isLibrarian && (
          <button onClick={() => navigate('/admin')} style={button(colors.dark)}>Панель библиотекаря ↗</button>
        )}
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

      {books.length === 0 ? (
        <p style={{ color: colors.muted }}>Ничего не найдено.</p>
      ) : (
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
      )}

      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 16, margin: '24px 0' }}>
        <button
          onClick={() => loadPage(offset - PAGE_SIZE, activeQuery)}
          disabled={!canPrev}
          style={{ ...button(colors.dark), opacity: canPrev ? 1 : 0.4, cursor: canPrev ? 'pointer' : 'default' }}
        >
          ← Назад
        </button>
        <span style={{ color: colors.muted }}>
          Страница {currentPage} из {totalPages} (всего {total})
        </span>
        <button
          onClick={() => loadPage(offset + PAGE_SIZE, activeQuery)}
          disabled={!canNext}
          style={{ ...button(colors.dark), opacity: canNext ? 1 : 0.4, cursor: canNext ? 'pointer' : 'default' }}
        >
          Вперёд →
        </button>
      </div>
    </div>
  );
};

export default BookSearchPage;
