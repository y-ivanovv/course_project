import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';

const RegisterPage = () => {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const navigate = useNavigate();
  const [error, setError] = useState('');

  const onSubmit = async (data) => {
    try {
      await libraryApi.createUser(data);
      alert('Регистрация прошла успешно!');
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка регистрации');
    }
  };

  return (
    <div style={styles.container}>
      <h2>Регистрация</h2>
      {error && <div style={styles.error}>{error}</div>}
      <form onSubmit={handleSubmit(onSubmit)} style={styles.form}>
        <div>
          <label>Имя:</label>
          <input type="text" {...register('name', { required: 'Имя обязательно' })} style={styles.input} />
          {errors.name && <p style={styles.errorText}>{errors.name.message}</p>}
        </div>
        <div>
          <label>Email:</label>
          <input type="email" {...register('email', { required: 'Email обязателен' })} style={styles.input} />
          {errors.email && <p style={styles.errorText}>{errors.email.message}</p>}
        </div>
        <div>
          <label>Пароль:</label>
          <input type="password" {...register('password', { required: 'Пароль обязателен', minLength: 4 })} style={styles.input} />
          {errors.password && <p style={styles.errorText}>Минимум 4 символа</p>}
        </div>
        <button type="submit" style={styles.btn}>Зарегистрироваться</button>
      </form>
    </div>
  );
};

const styles = {
  container: { maxWidth: '400px', margin: '40px auto', padding: '20px', border: '1px solid #ddd', borderRadius: '8px' },
  form: { display: 'flex', flexDirection: 'column', gap: '15px' },
  input: { width: '100%', padding: '8px', boxSizing: 'border-box' },
  btn: { padding: '10px', backgroundColor: '#2ecc71', color: 'white', border: 'none', cursor: 'pointer' },
  error: { color: 'red', marginBottom: '10px' },
  errorText: { color: 'red', fontSize: '12px', margin: '2px 0 0' }
};

export default RegisterPage;