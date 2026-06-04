import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';
import { useAuth } from '../context/authContext.js';
import { ui, button } from '../styles/ui.js';

const LoginPage = () => {
  const { register, handleSubmit } = useForm();
  const navigate = useNavigate();
  const { login } = useAuth();
  const [error, setError] = useState('');

  const onSubmit = async (data) => {
    try {
      const response = await libraryApi.loginUser(data);
      login(response.data);
      navigate('/');
    } catch {
      setError('Неверный логин или пароль');
    }
  };

  return (
    <div style={ui.authCard}>
      <h2>Вход в библиотеку</h2>
      {error && <p style={ui.error}>{error}</p>}
      <form onSubmit={handleSubmit(onSubmit)} style={ui.form}>
        <div>
          <label style={ui.label}>Email:</label>
          <input type="email" {...register('email', { required: true })} style={ui.input} />
        </div>
        <div>
          <label style={ui.label}>Пароль:</label>
          <input type="password" {...register('password', { required: true })} style={ui.input} />
        </div>
        <button type="submit" style={button()}>Войти</button>
      </form>
    </div>
  );
};

export default LoginPage;
