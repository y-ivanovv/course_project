package ru.edu.project.foundation.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.edu.project.entity.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /**
     * Активная (невозвращённая) выдача для книги, если она есть.
     */
    Optional<Loan> findByBookIdAndReturnedAtIsNull(String bookId);

    /**
     * Все книги, которые сейчас на руках у пользователя.
     */
    List<Loan> findByUserIdAndReturnedAtIsNull(Long userId);
}
