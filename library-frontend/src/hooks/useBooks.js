import { useState, useEffect, useCallback } from 'react';
import { libraryApi } from '../api/libraryApi';

/**
 * Загрузка списка книг. Устраняет дублирование логики между страницами.
 * Первичная загрузка выполняется в эффекте (setState — только из асинхронного колбэка),
 * reload() предназначен для ручного обновления из обработчиков событий.
 */
export function useBooks() {
  const [books, setBooks] = useState([]);

  const reload = useCallback(async () => {
    try {
      const response = await libraryApi.getAllBooks();
      setBooks(response.data);
    } catch {
      setBooks([]);
    }
  }, []);

  useEffect(() => {
    let active = true;
    libraryApi.getAllBooks()
      .then((response) => { if (active) setBooks(response.data); })
      .catch(() => { if (active) setBooks([]); });
    return () => { active = false; };
  }, []);

  return { books, setBooks, reload };
}
