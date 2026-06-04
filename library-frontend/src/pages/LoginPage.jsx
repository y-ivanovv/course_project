import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';

const LoginPage = () => {
  const { register, handleSubmit } = useForm();
  const navigate = useNavigate();
  const [error, setError] = useState('');

  const onSubmit = async (data) => {
    try {
      const response = await libraryApi.loginUser(data);
      localStorage.setItem('user', JSON.stringify(response.data));
      alert('Вход выполнен!');
      navigate('/');
      window.location.reload();
    } catch (err) {
      setError('Неверный логин или пароль');
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
      <h2>Вход в библиотеку</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
        <div>
          <label>Email:</label>
          <input type="email" {...register('email', { required: true })} style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }} />
        </div>
        <div>
          <label>Пароль:</label>
          <input type="password" {...register('password', { required: true })} style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }} />
        </div>
        <button type="submit" style={{ padding: '10px', backgroundColor: '#3498db', color: 'white', border: 'none', cursor: 'pointer' }}>Войти</button>
      </form>
    </div>
  );
};

export default LoginPage;