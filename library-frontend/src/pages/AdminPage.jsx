import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { libraryApi } from '../api/libraryApi';
import { ui, button, colors } from '../styles/ui.js';

const cardStyle = { flex: 1, minWidth: 320, padding: 20, border: '1px solid #ccc', borderRadius: 8 };

const AdminPage = () => {
  // Форма добавления книги
  const { register, handleSubmit, reset, formState: { errors } } = useForm();
  const [bookMessage, setBookMessage] = useState(null);

  // Форма удаления книги по ISBN
  const {
    register: registerDel,
    handleSubmit: handleSubmitDel,
    reset: resetDel,
    formState: { errors: errorsDel },
  } = useForm();
  const [delMessage, setDelMessage] = useState(null);

  // Форма создания библиотекаря
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
      setBookMessage({ type: 'success', text: 'Книга добавлена и готова к поиску!' });
      reset();
    } catch (err) {
      setBookMessage({ type: 'error', text: err.response?.data?.message || 'Ошибка добавления книги' });
    }
  };

  const onDeleteByIsbn = async (data) => {
    if (!window.confirm(`Удалить книгу с ISBN ${data.isbn}?`)) {
      return;
    }
    try {
      await libraryApi.deleteBookByIsbn(data.isbn);
      setDelMessage({ type: 'success', text: `Книга с ISBN ${data.isbn} удалена` });
      resetDel();
    } catch (err) {
      setDelMessage({ type: 'error', text: err.response?.data?.message || 'Ошибка удаления книги' });
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
    <div style={{ maxWidth: 1000, margin: '30px auto', ...ui.page }}>
      <h2>Панель библиотекаря</h2>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 24 }}>
        {/* Добавление книги */}
        <div style={cardStyle}>
          <h3>Добавить книгу</h3>
          {bookMessage && (
            <div style={bookMessage.type === 'success' ? ui.success : ui.error}>{bookMessage.text}</div>
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

        {/* Удаление книги по ISBN */}
        <div style={cardStyle}>
          <h3>Удалить книгу по ISBN</h3>
          {delMessage && (
            <div style={delMessage.type === 'success' ? ui.success : ui.error}>{delMessage.text}</div>
          )}
          <form onSubmit={handleSubmitDel(onDeleteByIsbn)} style={ui.form}>
            <div>
              <label style={ui.label}>ISBN *</label>
              <input type="text" {...registerDel('isbn', { required: 'Введите ISBN' })} style={ui.input} />
              {errorsDel.isbn && <span style={ui.errorText}>{errorsDel.isbn.message}</span>}
            </div>
            <button type="submit" style={button(colors.danger)}>Удалить книгу</button>
          </form>
        </div>

        {/* Создание библиотекаря */}
        <div style={cardStyle}>
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
    </div>
  );
};

export default AdminPage;
