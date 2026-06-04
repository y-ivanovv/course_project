package ru.edu.project.control.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ru.edu.project.control.dto.BookCreateRequest;
import ru.edu.project.control.dto.BookSearchRequest;
import ru.edu.project.control.dto.PageResponse;
import ru.edu.project.entity.Book;
import ru.edu.project.entity.Loan;
import ru.edu.project.foundation.security.AuthInterceptor;
import ru.edu.project.foundation.security.RequireRole;
import ru.edu.project.mediator.interfaces.BookService;
import ru.edu.project.mediator.interfaces.LoanService;

/**
 * Авторизация (наличие сессии + роли) обеспечивается AuthInterceptor,
 * обработка ошибок — GlobalExceptionHandler. Контроллер содержит только маршрутизацию.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final String ROLE_LIBRARIAN = "LIBRARIAN";
    private static final int DEFAULT_LIMIT = 12;
    private static final int MAX_LIMIT = 100;

    private final BookService bookService;
    private final LoanService loanService;

    @Autowired
    public BookController(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }

    // Постраничный список книг
    @GetMapping
    public PageResponse<Book> getAll(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "12") int limit) {
        int safeLimit = clampLimit(limit);
        int safeOffset = Math.max(offset, 0);
        return PageResponse.of(bookService.getBooks(toPageable(safeOffset, safeLimit)), safeOffset, safeLimit);
    }

    // Получить книгу по ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable String id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Постраничный полнотекстовый поиск
    @PostMapping("/search")
    public PageResponse<Book> search(
            @Valid @RequestBody BookSearchRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "12") int limit) {
        int safeLimit = clampLimit(limit);
        int safeOffset = Math.max(offset, 0);
        return PageResponse.of(
                bookService.searchBooks(request.getQuery(), toPageable(safeOffset, safeLimit)),
                safeOffset, safeLimit);
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

    // Удаление книги по ID (только LIBRARIAN)
    @DeleteMapping("/{id}")
    @RequireRole(ROLE_LIBRARIAN)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Удаление книги по ISBN (только LIBRARIAN)
    @DeleteMapping("/by-isbn/{isbn}")
    @RequireRole(ROLE_LIBRARIAN)
    public ResponseEntity<Void> deleteByIsbn(@PathVariable String isbn) {
        bookService.deleteByIsbn(isbn);
        return ResponseEntity.noContent().build();
    }

    // Выдать книгу текущему пользователю
    @PostMapping("/{id}/borrow")
    public Loan borrowBook(@PathVariable String id, HttpSession session) {
        return loanService.borrowBook(id, currentUserId(session));
    }

    // Вернуть книгу (только тот, кто её взял)
    @PostMapping("/{id}/return")
    public Loan returnBook(@PathVariable String id, HttpSession session) {
        return loanService.returnBook(id, currentUserId(session));
    }

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute(AuthInterceptor.SESSION_USER);
    }

    private int clampLimit(int limit) {
        if (limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private Pageable toPageable(int offset, int limit) {
        // Преобразуем offset/limit в постраничный запрос Spring Data
        return PageRequest.of(offset / limit, limit);
    }
}
