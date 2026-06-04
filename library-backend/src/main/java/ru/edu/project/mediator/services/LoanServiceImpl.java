package ru.edu.project.mediator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.edu.project.entity.Book;
import ru.edu.project.entity.Loan;
import ru.edu.project.foundation.repositories.BookRepository;
import ru.edu.project.foundation.repositories.LoanRepository;
import ru.edu.project.mediator.exceptions.ForbiddenOperationException;
import ru.edu.project.mediator.exceptions.ResourceNotFoundException;
import ru.edu.project.mediator.interfaces.LoanService;

/**
 * Логика выдачи/возврата книг. Связь «книга — пользователь» хранится в таблице loans (PostgreSQL),
 * статус доступности книги — в Elasticsearch. Источник правды о том, кто держит книгу, — таблица loans.
 *
 * Примечание: PostgreSQL и Elasticsearch не делят единую транзакцию. Запись о выдаче (PostgreSQL)
 * фиксируется в рамках @Transactional; обновление статуса в Elasticsearch выполняется в том же методе,
 * и при его сбое транзакция PostgreSQL откатывается.
 */
@Service
public class LoanServiceImpl implements LoanService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @Autowired
    public LoanServiceImpl(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    @Transactional
    public Loan borrowBook(String bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Книга не найдена"));

        loanRepository.findByBookIdAndReturnedAtIsNull(bookId).ifPresent(active -> {
            throw new IllegalStateException("Книга уже выдана");
        });

        // Инвариант статуса проверяется и устанавливается в самой сущности
        book.borrowBook();
        bookRepository.save(book);

        return loanRepository.save(new Loan(bookId, userId));
    }

    @Override
    @Transactional
    public Loan returnBook(String bookId, Long userId) {
        Loan loan = loanRepository.findByBookIdAndReturnedAtIsNull(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Активная выдача книги не найдена"));

        // Ключевая проверка: вернуть книгу может только тот, кто её взял
        if (!loan.getUserId().equals(userId)) {
            throw new ForbiddenOperationException("Вернуть книгу может только пользователь, который её взял");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Книга не найдена"));
        book.returnBook();
        bookRepository.save(book);

        loan.markReturned();
        return loanRepository.save(loan);
    }
}
