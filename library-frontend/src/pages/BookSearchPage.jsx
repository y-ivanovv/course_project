import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';

const BookSearchPage = () => {
  const [books, setBooks] = useState([]);
  const [query, setQuery] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    loadAllBooks();
  }, []);

  const loadAllBooks = async () => {
    try {
      const response = await libraryApi.getAllBooks();
      setBooks(response.data);
    } catch (err) {
      console.error('Ошибка загрузки книг', err);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query.trim()) {
      loadAllBooks();
      return;
    }
    try {
      const response = await libraryApi.searchBooks(query);
      setBooks(response.data);
    } catch (err) {
      console.error('Ошибка поиска', err);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '30px auto', fontFamily: 'Arial' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Полнотекстовый поиск книг (Elasticsearch)</h2>
        <button onClick={() => navigate('/admin')} style={{ padding: '8px', backgroundColor: '#2c3e50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Панель библиотекаря ↗</button>
      </div>

      <form onSubmit={handleSearch} style={{ display: 'flex', gap: '10px', margin: '20px 0' }}>
        <input 
          type="text" 
          placeholder="Введите название или описание книги (например: руководству)..." 
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          style={{ flex: 1, padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
        />
        <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Искать</button>
      </form>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
        {books.map(book => (
          <div key={book.id} style={{ padding: '15px', border: '1px solid #eee', borderRadius: '6px', backgroundColor: '#fff', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}>
            <h3>{book.title}</h3>
            <p><strong>Автор:</strong> {book.author}</p>
            <p style={{ color: '#666', fontSize: '14px' }}>{book.description?.substring(0, 80)}...</p>
            <span style={{ padding: '4px 8px', borderRadius: '4px', fontSize: '12px', color: 'white', backgroundColor: book.status === 'AVAILABLE' ? '#27ae60' : '#e67e22' }}>
              {book.status}
            </span>
            <button onClick={() => navigate(`/books/${book.id}`)} style={{ display: 'block', marginTop: '10px', padding: '6px 12px', backgroundColor: '#f1f1f1', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Подробнее</button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default BookSearchPage;