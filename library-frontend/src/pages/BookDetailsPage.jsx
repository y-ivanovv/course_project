import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';
import { ui, button, colors } from '../styles/ui.js';

const BookDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [message, setMessage] = useState(null);

  // Используется обработчиками «взять/вернуть» для обновления карточки после действия.
  const reloadBook = useCallback(async () => {
    try {
      const response = await libraryApi.getBookById(id);
      setBook(response.data);
    } catch {
      navigate('/');
    }
  }, [id, navigate]);

  // Первичная загрузка: setState только из асинхронного колбэка.
  useEffect(() => {
    let active = true;
    libraryApi.getBookById(id)
      .then((response) => { if (active) setBook(response.data); })
      .catch(() => { if (active) navigate('/'); });
    return () => { active = false; };
  }, [id, navigate]);

  const handleBorrow = async () => {
    try {
      await libraryApi.borrowBook(id);
      setMessage({ type: 'success', text: 'Вы успешно взяли книгу!' });
      reloadBook();
    } catch {
      setMessage({ type: 'error', text: 'Не удалось взять книгу' });
    }
  };

  const handleReturn = async () => {
    try {
      await libraryApi.returnBook(id);
      setMessage({ type: 'success', text: 'Книга возвращена на склад!' });
      reloadBook();
    } catch {
      setMessage({ type: 'error', text: 'Ошибка при возврате книги' });
    }
  };

  if (!book) return <div style={{ textAlign: 'center', marginTop: 5 }}>Загрузка...</div>;

  return (
    <div style={{ maxWidth: 600, margin: '40px auto', padding: 20, border: '1px solid #ddd', borderRadius: 8, ...ui.page }}>
      <button onClick={() => navigate('/')} style={{ ...ui.linkButton, marginBottom: 20 }}>← Назад к поиску</button>
      <h2>{book.title}</h2>
      <p><strong>Автор:</strong> {book.author}</p>
      <p><strong>ISBN:</strong> {book.isbn}</p>
      <p><strong>Жанр:</strong> {book.genre}</p>
      <p><strong>Описание:</strong> {book.description}</p>
      <p>
        <strong>Статус: </strong>
        <span style={{ color: book.status === 'AVAILABLE' ? colors.available : colors.borrowed, fontWeight: 'bold' }}>
          {book.status}
        </span>
      </p>

      {message && <div style={message.type === 'success' ? ui.success : ui.error}>{message.text}</div>}

      <div style={{ marginTop: 20, display: 'flex', gap: 10 }}>
        {book.status === 'AVAILABLE' ? (
          <button onClick={handleBorrow} style={button(colors.available)}>Взять читать</button>
        ) : (
          <button onClick={handleReturn} style={button(colors.borrowed)}>Вернуть книгу</button>
        )}
      </div>
    </div>
  );
};

export default BookDetailsPage;
