import api from './axios';

export const libraryApi = {
  // Книги (Elasticsearch)
  getAllBooks: (offset = 0, limit = 12) => api.get('/books', { params: { offset, limit } }),
  getBookById: (id) => api.get(`/books/${id}`),
  searchBooks: (query, offset = 0, limit = 12) => api.post('/books/search', { query }, { params: { offset, limit } }),
  createBook: (bookData) => api.post('/books', bookData),
  deleteBookByIsbn: (isbn) => api.delete(`/books/by-isbn/${isbn}`),
  borrowBook: (id) => api.post(`/books/${id}/borrow`),
  returnBook: (id) => api.post(`/books/${id}/return`),

  // Пользователи (PostgreSQL)
  createUser: (userData) => api.post('/users/register', userData),
  createLibrarian: (userData) => api.post('/users/librarians', userData),
  loginUser: (credentials) => api.post('/users/login', credentials),
  logoutUser: () => api.post('/users/logout'),
};