package ru.edu.project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Факт выдачи книги конкретному пользователю.
 * Хранится в PostgreSQL и является источником правды о том, кто держит книгу.
 * Активная (невозвращённая) выдача — это запись с returnedAt == null.
 */
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Идентификатор книги в Elasticsearch (строковый)
    @Column(name = "book_id", nullable = false)
    private String bookId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "borrowed_at", nullable = false)
    private LocalDateTime borrowedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    // Пустой конструктор (требование JPA)
    protected Loan() {}

    public Loan(String bookId, Long userId) {
        this.bookId = bookId;
        this.userId = userId;
        this.borrowedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return returnedAt == null;
    }

    /**
     * Бизнес-метод: фиксирует возврат книги.
     */
    public void markReturned() {
        if (!isActive()) {
            throw new IllegalStateException("Книга уже возвращена");
        }
        this.returnedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getBookId() { return bookId; }
    public Long getUserId() { return userId; }
    public LocalDateTime getBorrowedAt() { return borrowedAt; }
    public LocalDateTime getReturnedAt() { return returnedAt; }
}
