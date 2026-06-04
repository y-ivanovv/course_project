import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';

const BookDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);

  useEffect(() => {
    loadBook();
  }, [id]);

  const loadBook = async () => {
    try {
      const response = await libraryApi.getBookById(id);
      setBook(response.data);
    } catch (err) {
      alert('Книга не найдена');
      navigate('/');
    }
  };

  const handleBorrow = async () => {
    try {
      await libraryApi.borrowBook(id);
      alert('Вы успешно взяли книгу!');
      loadBook();
    } catch (err) {
      alert('Не удалось взять книгу');
    }
  };

  const handleReturn = async () => {
    try {
      await libraryApi.returnBook(id);
      alert('Книга возвращена на склад!');
      loadBook();
    } catch (err) {
      alert('Ошибка при возврате книги');
    }
  };

  if (!book) return <div style={{ textAlign: 'center', marginTop: '5px' }}>Загрузка...</div>;

  return (
    <div style={{ maxWidth: '600px', margin: '40px auto', padding: '20px', border: '1px solid #ddd', borderRadius: '8px', fontFamily: 'Arial' }}>
      <button onClick={() => navigate('/')} style={{ marginBottom: '20px', background: 'none', border: 'none', color: '#3498db', cursor: 'pointer' }}>← Назад к поиску</button>
      <h2>{book.title}</h2>
      <p><strong>Автор:</strong> {book.author}</p>
      <p><strong>ISBN:</strong> {book.isbn}</p>
      <p><strong>Жанр:</strong> {book.genre}</p>
      <p><strong>Описание:</strong> {book.description}</p>
      <p>
        <strong>Статус: </strong> 
        <span style={{ color: book.status === 'AVAILABLE' ? '#27ae60' : '#e67e22', fontWeight: 'bold' }}>{book.status}</span>
      </p>

      <div style={{ marginTop: '20px', display: 'flex', gap: '10px' }}>
        {book.status === 'AVAILABLE' ? (
          <button onClick={handleBorrow} style={{ padding: '10px 20px', backgroundColor: '#27ae60', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Взять читать</button>
        ) : (
          <button onClick={handleReturn} style={{ padding: '10px 20px', backgroundColor: '#e67e22', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Вернуть книгу</button>
        )}
      </div>
    </div>
  );
};

export default BookDetailsPage;