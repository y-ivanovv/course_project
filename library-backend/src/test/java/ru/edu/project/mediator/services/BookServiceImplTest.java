package ru.edu.project.mediator.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        // Инициализируем тестовую книгу перед каждым тестом
        testBook = new Book("Чистый код", "Роберт Мартин", "12345", "Руководство по рефакторингу", "Программирование");
        testBook.setId("test-id-123");
    }

    // 1. Тест успешного создания книги
    @Test
    void addBook_Success() {
        // Arrange
        when(bookRepository.findByIsbn("12345")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        Book createdBook = bookService.addBook("Чистый код", "Роберт Мартин", "12345", "Руководство по рефакторингу", "Программирование");

        // Assert
        assertNotNull(createdBook);
        assertEquals("12345", createdBook.getIsbn());
        assertEquals("AVAILABLE", createdBook.getStatus());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    // 2. Тест ошибки при создании книги с дублирующимся ISBN
    @Test
    void addBook_ThrowsException_WhenIsbnExists() {
        // Arrange
        when(bookRepository.findByIsbn("12345")).thenReturn(Optional.of(testBook));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.addBook("Чистый код", "Роберт Мартин", "12345", "Руководство по рефакторингу", "Программирование");
        });

        assertEquals("Книга с таким ISBN уже существует!", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    // 3. Тест успешной выдачи книги
    @Test
    void borrowBook_Success() {
        // Arrange
        when(bookRepository.findById("test-id-123")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Book borrowedBook = bookService.borrowBook("test-id-123");

        // Assert
        assertEquals("BORROWED", borrowedBook.getStatus());
        verify(bookRepository, times(1)).save(testBook);
    }

    // 4. Тест ошибки при попытке взять уже выданную книгу
    @Test
    void borrowBook_ThrowsException_WhenAlreadyBorrowed() {
        // Arrange
        testBook.setStatus("BORROWED"); // Книга уже занята
        when(bookRepository.findById("test-id-123")).thenReturn(Optional.of(testBook));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookService.borrowBook("test-id-123");
        });

        assertTrue(exception.getMessage().contains("уже выдана"));
        verify(bookRepository, never()).save(any(Book.class));
    }

    // 5. Тест успешного возврата книги
    @Test
    void returnBook_Success() {
        // Arrange
        testBook.setStatus("BORROWED"); // Книга была выдана
        when(bookRepository.findById("test-id-123")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Book returnedBook = bookService.returnBook("test-id-123");

        // Assert
        assertEquals("AVAILABLE", returnedBook.getStatus());
        verify(bookRepository, times(1)).save(testBook);
    }

    // 6. Тест удаления несуществующей книги
    @Test
    void deleteBook_ThrowsException_WhenNotFound() {
        // Arrange
        when(bookRepository.existsById("non-existent-id")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.deleteBook("non-existent-id");
        });

        assertEquals("Книга не найдена", exception.getMessage());
        verify(bookRepository, never()).deleteById(anyString());
    }
}