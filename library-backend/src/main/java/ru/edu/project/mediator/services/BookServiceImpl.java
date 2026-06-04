package ru.edu.project.mediator.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.edu.project.entity.Book;
import ru.edu.project.foundation.repositories.BookRepository;
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
            throw new IllegalArgumentException("Книга с таким ISBN уже существует!");
        }
        Book book = new Book(title, author, isbn, description, genre);
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
        return books;
    }

    @Override
    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }
        return bookRepository.searchByQuery(query);
    }

    @Override
    public void deleteBook(String id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Книга не найдена");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public Book borrowBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Книга не найдена"));
        book.borrowBook(); 
        return bookRepository.save(book);
    }

    @Override
    public Book returnBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Книга не найдена"));
        book.returnBook(); 
        return bookRepository.save(book);
    }
}