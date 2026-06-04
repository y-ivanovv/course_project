package ru.edu.project.mediator.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.edu.project.entity.Book;
import ru.edu.project.foundation.repositories.BookRepository;
import ru.edu.project.mediator.exceptions.DuplicateResourceException;
import ru.edu.project.mediator.exceptions.ResourceNotFoundException;
import ru.edu.project.mediator.interfaces.BookService;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(String title, String author, String isbn, String description, String genre) {
        if (bookRepository.findByIsbn(isbn).isPresent()) {
            throw new DuplicateResourceException("Книга с таким ISBN уже существует!");
        }
        Book book = new Book(title, author, isbn, description, genre);
        return bookRepository.save(book);
    }

    @Override
    public Page<Book> getBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public Page<Book> searchBooks(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return Page.empty(pageable);
        }
        return bookRepository.searchByQuery(query, pageable);
    }

    @Override
    public void deleteBook(String id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Книга не найдена");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public void deleteByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ISBN " + isbn + " не найдена"));
        bookRepository.deleteById(book.getId());
    }
}
