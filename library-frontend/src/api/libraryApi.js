import api from './axios';

export const libraryApi = {
  // Книги (Elasticsearch)
  getAllBooks: () => api.get('/books'),
  getBookById: (id) => api.get(`/books/${id}`),
  searchBooks: (query) => api.post('/books/search', { query }),
  createBook: (bookData) => api.post('/books', bookData),
  deleteBook: (id) => api.delete(`/books/${id}`),
  borrowBook: (id) => api.post(`/books/${id}/borrow`),
  returnBook: (id) => api.post(`/books/${id}/return`),

  // Пользователи (PostgreSQL)
  createUser: (userData) => api.post('/users/register', userData),
  createLibrarian: (userData) => api.post('/users/librarians', userData),
  loginUser: (credentials) => api.post('/users/login', credentials),
  logoutUser: () => api.post('/users/logout'),
};