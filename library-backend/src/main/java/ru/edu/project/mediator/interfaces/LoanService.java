package ru.edu.project.mediator.interfaces;

import ru.edu.project.entity.Loan;

public interface LoanService {

    /**
     * Выдать книгу пользователю. Создаёт запись о выдаче и помечает книгу как выданную.
     */
    Loan borrowBook(String bookId, Long userId);

    /**
     * Вернуть книгу. Вернуть может только тот пользователь, который её взял.
     */
    Loan returnBook(String bookId, Long userId);
}
