package ru.edu.project.control.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ru.edu.project.control.dto.BookCreateRequest;
import ru.edu.project.control.dto.BookSearchRequest;
import ru.edu.project.entity.Book;
import ru.edu.project.mediator.interfaces.BookService;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 1. Получить все книги (Для всех авторизованных)
    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Авторизуйтесь в системе");
        }
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // 2. Получить книгу по ID (Для всех авторизованных)
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. ПОИСК ЧЕРЕЗ POST (Для всех авторизованных)
    @PostMapping("/search")
    public ResponseEntity<?> search(@Valid @RequestBody BookSearchRequest request, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(bookService.searchBooks(request.getQuery()));
    }

    // 4. Создание книги (ТОЛЬКО ДЛЯ LIBRARIAN)
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BookCreateRequest request, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String role = (String) session.getAttribute("userRole");
        if (!"LIBRARIAN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ только для библиотекарей");
        }

        Book book = bookService.addBook(
                request.getTitle(), 
                request.getAuthor(), 
                request.getIsbn(), 
                request.getDescription(), 
                request.getGenre()
        );
        return ResponseEntity.ok(book);
    }

    // 5. Удаление книги (ТОЛЬКО ДЛЯ LIBRARIAN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String role = (String) session.getAttribute("userRole");
        if (!"LIBRARIAN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. Выдать книгу (Для всех авторизованных)
    @PostMapping("/{id}/borrow")
    public ResponseEntity<?> borrowBook(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Book book = bookService.borrowBook(id);
            return ResponseEntity.ok(book);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 7. Возврат книги (Для всех авторизованных)
    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Book book = bookService.returnBook(id);
            return ResponseEntity.ok(book);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}