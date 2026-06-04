import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { libraryApi } from '../api/libraryApi';
import { useBooks } from '../hooks/useBooks.js';
import { ui, button, colors } from '../styles/ui.js';

const AdminPage = () => {
  const { books, reload } = useBooks();
  const { register, handleSubmit, reset, formState: { errors } } = useForm();
  const [message, setMessage] = useState(null);

  // Отдельная форма и сообщение для создания библиотекаря
  const {
    register: registerLib,
    handleSubmit: handleSubmitLib,
    reset: resetLib,
    formState: { errors: errorsLib },
  } = useForm();
  const [libMessage, setLibMessage] = useState(null);

  const onAddBook = async (data) => {
    try {
      await libraryApi.createBook(data);
      setMessage({ type: 'success', text: 'Книга добавлена и готова к поиску!' });
      reset();
      reload();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Ошибка добавления книги' });
    }
  };

  const onDeleteBook = async (id) => {
    if (!window.confirm('Вы действительно хотите удалить эту книгу из базы?')) {
      return;
    }
    try {
      await libraryApi.deleteBook(id);
      reload();
    } catch {
      setMessage({ type: 'error', text: 'Ошибка при удалении' });
    }
  };

  const onCreateLibrarian = async (data) => {
    try {
      await libraryApi.createLibrarian(data);
      setLibMessage({ type: 'success', text: `Библиотекарь ${data.email} создан` });
      resetLib();
    } catch (err) {
      setLibMessage({ type: 'error', text: err.response?.data?.message || 'Ошибка создания библиотекаря' });
    }
  };

  return (
    <div style={{ maxWidth: 900, margin: '30px auto', ...ui.page }}>
      <div style={{ display: 'flex', gap: 30 }}>
        {/* Форма добавления книги */}
        <div style={{ flex: 1, padding: 20, border: '1px solid #ccc', borderRadius: 8 }}>
          <h3>Добавить книгу (Библиотекарь)</h3>
          {message && (
            <div style={message.type === 'success' ? ui.success : ui.error}>{message.text}</div>
          )}
          <form onSubmit={handleSubmit(onAddBook)} style={ui.form}>
            <div>
              <label style={ui.label}>Название *</label>
              <input type="text" {...register('title', { required: 'Введите название' })} style={ui.input} />
              {errors.title && <span style={ui.errorText}>{errors.title.message}</span>}
            </div>
            <div>
              <label style={ui.label}>Автор *</label>
              <input type="text" {...register('author', { required: 'Укажите автора' })} style={ui.input} />
              {errors.author && <span style={ui.errorText}>{errors.author.message}</span>}
            </div>
            <div>
              <label style={ui.label}>ISBN *</label>
              <input type="text" {...register('isbn', { required: 'ISBN обязателен' })} style={ui.input} />
              {errors.isbn && <span style={ui.errorText}>{errors.isbn.message}</span>}
            </div>
            <div>
              <label style={ui.label}>Жанр</label>
              <input type="text" {...register('genre')} style={ui.input} />
            </div>
            <div>
              <label style={ui.label}>Описание</label>
              <textarea {...register('description')} rows="3" style={ui.input} />
            </div>
            <button type="submit" style={button(colors.success)}>Сохранить книгу</button>
          </form>
        </div>

        {/* Список для удаления */}
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
              {books.map((book) => (
                <tr key={book.id} style={{ borderBottom: '1px solid #eee' }}>
                  <td style={{ padding: '8px 0' }}>{book.title}</td>
                  <td>{book.isbn}</td>
                  <td>
                    <button onClick={() => onDeleteBook(book.id)} style={button(colors.danger)}>Удалить</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Создание учётной записи библиотекаря */}
      <div style={{ marginTop: 30, maxWidth: 420, padding: 20, border: '1px solid #ccc', borderRadius: 8 }}>
        <h3>Добавить библиотекаря</h3>
        {libMessage && (
          <div style={libMessage.type === 'success' ? ui.success : ui.error}>{libMessage.text}</div>
        )}
        <form onSubmit={handleSubmitLib(onCreateLibrarian)} style={ui.form}>
          <div>
            <label style={ui.label}>Имя *</label>
            <input type="text" {...registerLib('name', { required: 'Введите имя' })} style={ui.input} />
            {errorsLib.name && <span style={ui.errorText}>{errorsLib.name.message}</span>}
          </div>
          <div>
            <label style={ui.label}>Email *</label>
            <input type="email" {...registerLib('email', { required: 'Введите email' })} style={ui.input} />
            {errorsLib.email && <span style={ui.errorText}>{errorsLib.email.message}</span>}
          </div>
          <div>
            <label style={ui.label}>Пароль *</label>
            <input
              type="password"
              {...registerLib('password', { required: 'Введите пароль', minLength: 6 })}
              style={ui.input}
            />
            {errorsLib.password && <span style={ui.errorText}>Минимум 6 символов</span>}
          </div>
          <button type="submit" style={button()}>Создать библиотекаря</button>
        </form>
      </div>
    </div>
  );
};

export default AdminPage;
