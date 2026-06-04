package ru.edu.project.mediator.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.edu.project.entity.Book;
import ru.edu.project.foundation.repositories.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Чистый код", "Роберт Мартин", "12345", "Руководство по рефакторингу", "Программирование");
        testBook.setId("test-id-123");
    }

    // 1. Успешное создание книги
    @Test
    void addBook_Success() {
        when(bookRepository.findByIsbn("12345")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book createdBook = bookService.addBook("Чистый код", "Роберт Мартин", "12345", "Руководство по рефакторингу", "Программирование");

        assertNotNull(createdBook);
        assertEquals("12345", createdBook.getIsbn());
        assertEquals("AVAILABLE", createdBook.getStatus());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    // 2. Ошибка при создании книги с дублирующимся ISBN
    @Test
    void addBook_ThrowsException_WhenIsbnExists() {
        when(bookRepository.findByIsbn("12345")).thenReturn(Optional.of(testBook));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("Чистый код", "Роберт Мартин", "12345", "Руководство по рефакторингу", "Программирование"));

        assertEquals("Книга с таким ISBN уже существует!", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    // 3. Удаление несуществующей книги
    @Test
    void deleteBook_ThrowsException_WhenNotFound() {
        when(bookRepository.existsById("non-existent-id")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.deleteBook("non-existent-id"));

        assertEquals("Книга не найдена", exception.getMessage());
        verify(bookRepository, never()).deleteById(anyString());
    }
}
