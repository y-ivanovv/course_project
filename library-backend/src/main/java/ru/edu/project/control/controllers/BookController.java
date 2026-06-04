package ru.edu.project.control.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.edu.project.control.dto.BookCreateRequest;
import ru.edu.project.control.dto.BookSearchRequest;
import ru.edu.project.entity.Book;
import ru.edu.project.foundation.security.RequireRole;
import ru.edu.project.mediator.interfaces.BookService;

/**
 * Авторизация (наличие сессии + роли) обеспечивается AuthInterceptor,
 * обработка ошибок — GlobalExceptionHandler. Контроллер содержит только маршрутизацию.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final String ROLE_LIBRARIAN = "LIBRARIAN";

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Получить все книги
    @GetMapping
    public List<Book> getAll() {
        return bookService.getAllBooks();
    }

    // Получить книгу по ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable String id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Полнотекстовый поиск
    @PostMapping("/search")
    public List<Book> search(@Valid @RequestBody BookSearchRequest request) {
        return bookService.searchBooks(request.getQuery());
    }

    // Создание книги (только LIBRARIAN)
    @PostMapping
    @RequireRole(ROLE_LIBRARIAN)
    public Book create(@Valid @RequestBody BookCreateRequest request) {
        return bookService.addBook(
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                request.getDescription(),
                request.getGenre());
    }

    // Удаление книги (только LIBRARIAN)
    @DeleteMapping("/{id}")
    @RequireRole(ROLE_LIBRARIAN)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Выдать книгу
    @PostMapping("/{id}/borrow")
    public Book borrowBook(@PathVariable String id) {
        return bookService.borrowBook(id);
    }

    // Вернуть книгу
    @PostMapping("/{id}/return")
    public Book returnBook(@PathVariable String id) {
        return bookService.returnBook(id);
    }
}
