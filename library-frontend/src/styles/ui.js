// Централизованные стили вместо повторяющихся инлайнов в каждом компоненте.

export const colors = {
  primary: '#3498db',
  dark: '#2c3e50',
  success: '#2ecc71',
  danger: '#e74c3c',
  available: '#27ae60',
  borrowed: '#e67e22',
  muted: '#666',
};

export const ui = {
  page: { fontFamily: 'Arial, sans-serif' },
  authCard: {
    maxWidth: 400, margin: '40px auto', padding: 20,
    border: '1px solid #ddd', borderRadius: 8,
  },
  card: {
    padding: 15, border: '1px solid #eee', borderRadius: 6,
    backgroundColor: '#fff', boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
  },
  form: { display: 'flex', flexDirection: 'column', gap: 12 },
  input: {
    width: '100%', padding: 8, boxSizing: 'border-box',
    borderRadius: 4, border: '1px solid #ccc',
  },
  label: { display: 'block', marginBottom: 4, fontSize: 14 },
  error: { color: colors.danger, marginBottom: 10 },
  errorText: { color: colors.danger, fontSize: 12, margin: '2px 0 0' },
  success: { color: colors.available, marginBottom: 10 },
  linkButton: { background: 'none', border: 'none', color: colors.primary, cursor: 'pointer', padding: 0 },
};

// Кнопка с настраиваемым цветом фона.
export const button = (background = colors.primary) => ({
  padding: '10px 16px',
  backgroundColor: background,
  color: 'white',
  border: 'none',
  borderRadius: 4,
  cursor: 'pointer',
});

// Бейдж статуса книги.
export const statusBadge = (status) => ({
  padding: '4px 8px',
  borderRadius: 4,
  fontSize: 12,
  color: 'white',
  backgroundColor: status === 'AVAILABLE' ? colors.available : colors.borrowed,
});
