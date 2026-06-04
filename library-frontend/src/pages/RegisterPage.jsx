import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { libraryApi } from '../api/libraryApi';
import { ui, button, colors } from '../styles/ui.js';

const RegisterPage = () => {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const navigate = useNavigate();
  const [error, setError] = useState('');

  const onSubmit = async (data) => {
    try {
      await libraryApi.createUser(data);
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка регистрации');
    }
  };

  return (
    <div style={ui.authCard}>
      <h2>Регистрация</h2>
      {error && <div style={ui.error}>{error}</div>}
      <form onSubmit={handleSubmit(onSubmit)} style={ui.form}>
        <div>
          <label style={ui.label}>Имя:</label>
          <input type="text" {...register('name', { required: 'Имя обязательно' })} style={ui.input} />
          {errors.name && <p style={ui.errorText}>{errors.name.message}</p>}
        </div>
        <div>
          <label style={ui.label}>Email:</label>
          <input type="email" {...register('email', { required: 'Email обязателен' })} style={ui.input} />
          {errors.email && <p style={ui.errorText}>{errors.email.message}</p>}
        </div>
        <div>
          <label style={ui.label}>Пароль:</label>
          <input
            type="password"
            {...register('password', { required: 'Пароль обязателен', minLength: 6 })}
            style={ui.input}
          />
          {errors.password && <p style={ui.errorText}>Минимум 6 символов</p>}
        </div>
        <button type="submit" style={button(colors.success)}>Зарегистрироваться</button>
      </form>
    </div>
  );
};

export default RegisterPage;
