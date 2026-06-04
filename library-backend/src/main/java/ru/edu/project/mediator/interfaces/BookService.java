package ru.edu.project.mediator.interfaces;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.edu.project.entity.Book;

public interface BookService {
    Book addBook(String title, String author, String isbn, String description, String genre);

    Page<Book> getBooks(Pageable pageable);

    Optional<Book> getBookById(String id);

    Page<Book> searchBooks(String query, Pageable pageable);

    void deleteBook(String id);

    /**
     * Удаление книги по её ISBN.
     */
    void deleteByIsbn(String isbn);
}
