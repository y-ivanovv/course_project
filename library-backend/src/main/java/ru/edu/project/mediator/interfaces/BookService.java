package ru.edu.project.mediator.interfaces;

import java.util.List;
import java.util.Optional;

import ru.edu.project.entity.Book;

public interface BookService {
    Book addBook(String title, String author, String isbn, String description, String genre);
    
    List<Book> getAllBooks();
    
    Optional<Book> getBookById(String id);
    
    List<Book> searchBooks(String query);
    
    void deleteBook(String id);
    
    Book borrowBook(String id);
    
    Book returnBook(String id);
}