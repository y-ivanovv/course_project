import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { libraryApi } from '../api/libraryApi';

const AdminPage = () => {
  const [books, setBooks] = useState([]);
  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  useEffect(() => {
    loadBooks();
  }, []);

  const loadBooks = async () => {
    try {
      const response = await libraryApi.getAllBooks();
      setBooks(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const onAddBook = async (data) => {
    try {
      await libraryApi.createBook(data);
      alert('Книга добавлена в Elasticsearch и готова к поиску!');
      reset();
      loadBooks();
    } catch (err) {
      alert(err.response?.data?.message || 'Ошибка добавления книги');
    }
  };

  const onDeleteBook = async (id) => {
    if (window.confirm('Вы действительно хотите удалить эту книгу из базы?')) {
      try {
        await libraryApi.deleteBook(id);
        loadBooks();
      } catch (err) {
        alert('Ошибка при удалении');
      }
    }
  };

  return (
    <div style={{ maxWidth: '900px', margin: '30px auto', fontFamily: 'Arial', display: 'flex', gap: '30px' }}>
      
      {/* Левая часть: Форма добавления */}
      <div style={{ flex: 1, padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
        <h3>Добавить книгу (Библиотекарь)</h3>
        <form onSubmit={handleSubmit(onAddBook)} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <div>
            <label>Название *</label>
            <input type="text" {...register('title', { required: 'Введите название' })} style={{ width: '100%', padding: '6px' }} />
            {errors.title && <span style={{ color: 'red', fontSize: '12px' }}>{errors.title.message}</span>}
          </div>
          <div>
            <label>Автор *</label>
            <input type="text" {...register('author', { required: 'Укажите автора' })} style={{ width: '100%', padding: '6px' }} />
          </div>
          <div>
            <label>ISBN *</label>
            <input type="text" {...register('isbn', { required: 'ISBN обязателен' })} style={{ width: '100%', padding: '6px' }} />
          </div>
          <div>
            <label>Жанр</label>
            <input type="text" {...register('genre')} style={{ width: '100%', padding: '6px' }} />
          </div>
          <div>
            <label>Описание</label>
            <textarea {...register('description')} rows="3" style={{ width: '100%', padding: '6px' }} />
          </div>
          <button type="submit" style={{ padding: '10px', backgroundColor: '#2ecc71', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Сохранить книгу</button>
        </form>
      </div>

      {/* Правая часть: Список для удаления */}
      <div style={{ flex: 1.5 }}>
        <h3>Управление фондом</h3>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid #ccc', textAlign: 'left' }}>
              <th>Название</th>
              <th>ISBN</th>
              <th>Действие</th>
            </tr>
          </thead>
          <tbody>
            {books.map(book => (
              <tr key={book.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '8px 0' }}>{book.title}</td>
                <td>{book.isbn}</td>
                <td>
                  <button onClick={() => onDeleteBook(book.id)} style={{ padding: '4px 8px', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Удалить</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminPage;