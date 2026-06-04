package ru.edu.project.mediator.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.edu.project.entity.Book;
import ru.edu.project.entity.Loan;
import ru.edu.project.foundation.repositories.BookRepository;
import ru.edu.project.foundation.repositories.LoanRepository;
import ru.edu.project.mediator.exceptions.ForbiddenOperationException;
import ru.edu.project.mediator.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    private static final String BOOK_ID = "test-id-123";
    private static final Long BORROWER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Чистый код", "Роберт Мартин", "12345", "Руководство", "Программирование");
        testBook.setId(BOOK_ID);
    }

    // 1. Успешная выдача: статус книги меняется и создаётся запись о выдаче
    @Test
    void borrowBook_Success() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(testBook));
        when(loanRepository.findByBookIdAndReturnedAtIsNull(BOOK_ID)).thenReturn(Optional.empty());
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan loan = loanService.borrowBook(BOOK_ID, BORROWER_ID);

        assertNotNull(loan);
        assertEquals(BORROWER_ID, loan.getUserId());
        assertEquals(BOOK_ID, loan.getBookId());
        assertTrue(loan.isActive());
        assertEquals("BORROWED", testBook.getStatus());
        verify(bookRepository, times(1)).save(testBook);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    // 2. Нельзя выдать книгу, по которой уже есть активная выдача
    @Test
    void borrowBook_ThrowsException_WhenAlreadyBorrowed() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(testBook));
        when(loanRepository.findByBookIdAndReturnedAtIsNull(BOOK_ID))
                .thenReturn(Optional.of(new Loan(BOOK_ID, OTHER_USER_ID)));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> loanService.borrowBook(BOOK_ID, BORROWER_ID));

        assertTrue(ex.getMessage().contains("уже выдана"));
        verify(loanRepository, never()).save(any(Loan.class));
        verify(bookRepository, never()).save(any(Book.class));
    }

    // 3. Успешный возврат тем же пользователем
    @Test
    void returnBook_Success() {
        testBook.setStatus("BORROWED");
        Loan activeLoan = new Loan(BOOK_ID, BORROWER_ID);
        when(loanRepository.findByBookIdAndReturnedAtIsNull(BOOK_ID)).thenReturn(Optional.of(activeLoan));
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(testBook));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan returned = loanService.returnBook(BOOK_ID, BORROWER_ID);

        assertEquals("AVAILABLE", testBook.getStatus());
        assertNotNull(returned.getReturnedAt());
        verify(bookRepository, times(1)).save(testBook);
    }

    // 4. КЛЮЧЕВОЙ ТЕСТ: вернуть книгу не может другой пользователь
    @Test
    void returnBook_ThrowsForbidden_WhenReturnedByDifferentUser() {
        Loan activeLoan = new Loan(BOOK_ID, BORROWER_ID);
        when(loanRepository.findByBookIdAndReturnedAtIsNull(BOOK_ID)).thenReturn(Optional.of(activeLoan));

        assertThrows(ForbiddenOperationException.class,
                () -> loanService.returnBook(BOOK_ID, OTHER_USER_ID));

        verify(bookRepository, never()).save(any(Book.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    // 5. Возврат книги, по которой нет активной выдачи
    @Test
    void returnBook_ThrowsNotFound_WhenNoActiveLoan() {
        when(loanRepository.findByBookIdAndReturnedAtIsNull(BOOK_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loanService.returnBook(BOOK_ID, BORROWER_ID));

        verify(bookRepository, never()).save(any(Book.class));
    }
}
