# Этап 4. Детальное проектирование

## 1. Диаграммы последовательности

### 1.1. Выдача книги (UC6)

```mermaid
sequenceDiagram
    actor U as Читатель
    participant FE as React (Presentation)
    participant AI as AuthInterceptor
    participant BC as BookController
    participant LS as LoanService
    participant BR as BookRepository (ES)
    participant LR as LoanRepository (PG)

    U->>FE: «Взять читать»
    FE->>AI: POST /api/books/{id}/borrow (cookie сессии)
    AI->>AI: проверка сессии (401 если нет)
    AI->>BC: проброс запроса
    BC->>LS: borrowBook(bookId, userId)
    LS->>BR: findById(bookId)
    LS->>LR: findByBookIdAndReturnedAtIsNull(bookId)
    alt уже выдана
        LS-->>BC: IllegalStateException → 409
    else доступна
        LS->>BR: save(book.status = BORROWED)
        LS->>LR: save(new Loan(bookId, userId))
        LS-->>BC: Loan
        BC-->>FE: 200 + Loan
    end
```

### 1.2. Возврат книги (UC7) — защита «чужого» возврата

```mermaid
sequenceDiagram
    actor U as Читатель
    participant BC as BookController
    participant LS as LoanService
    participant LR as LoanRepository (PG)
    participant BR as BookRepository (ES)

    U->>BC: POST /api/books/{id}/return
    BC->>LS: returnBook(bookId, userId)
    LS->>LR: findByBookIdAndReturnedAtIsNull(bookId)
    alt активной выдачи нет
        LS-->>BC: ResourceNotFoundException → 404
    else выдачу оформил другой пользователь
        LS-->>BC: ForbiddenOperationException → 403
    else возвращает тот, кто взял
        LS->>BR: save(book.status = AVAILABLE)
        LS->>LR: save(loan.markReturned())
        LS-->>BC: Loan
        BC-->>U: 200 + Loan
    end
```

### 1.3. Полнотекстовый поиск (UC4)

```mermaid
sequenceDiagram
    actor U as Пользователь
    participant FE as React
    participant BC as BookController
    participant BS as BookService
    participant BR as BookRepository
    participant ES as Elasticsearch

    U->>FE: ввод запроса + «Искать»
    FE->>BC: POST /api/books/search?offset&limit {query}
    BC->>BS: searchBooks(query, Pageable)
    BS->>BR: searchByQuery(query, Pageable)
    BR->>ES: bool: multi_match(title,description,author,genre) + term(isbn)
    ES-->>BR: hits (страница)
    BR-->>BS: Page<Book>
    BS-->>BC: Page<Book>
    BC-->>FE: PageResponse{content,total,offset,limit}
```

## 2. Диаграмма классов проектирования

```mermaid
classDiagram
    class BookController {
        +getAll(offset, limit) PageResponse
        +getById(id)
        +search(req, offset, limit) PageResponse
        +create(req) Book
        +deleteByIsbn(isbn)
        +borrowBook(id, session) Loan
        +returnBook(id, session) Loan
    }
    class LoanServiceImpl {
        +borrowBook(bookId, userId) Loan
        +returnBook(bookId, userId) Loan
    }
    class BookServiceImpl {
        +addBook(...) Book
        +getBooks(pageable) Page
        +searchBooks(query, pageable) Page
        +deleteByIsbn(isbn)
    }
    class AuthInterceptor {
        +preHandle(req, resp, handler) boolean
    }
    class GlobalExceptionHandler

    BookController --> BookServiceImpl
    BookController --> LoanServiceImpl
    LoanServiceImpl --> BookRepository
    LoanServiceImpl --> LoanRepository
    BookServiceImpl --> BookRepository
    AuthInterceptor ..> BookController : guards
    GlobalExceptionHandler ..> BookController : advises
```

## 3. Спецификация ключевых методов

| Метод | Сигнатура | Контракт |
|-------|-----------|----------|
| Выдача | `Loan LoanService.borrowBook(String bookId, Long userId)` | Книга существует и доступна → создаёт выдачу, статус `BORROWED`. Иначе 404/409. |
| Возврат | `Loan LoanService.returnBook(String bookId, Long userId)` | Есть активная выдача этого пользователя → закрывает её, статус `AVAILABLE`. Иначе 404/403. |
| Поиск | `Page<Book> BookService.searchBooks(String query, Pageable p)` | Пустой запрос → пустая страница; иначе нечёткий поиск по 4 полям + точный по ISBN. |
| Аутентификация | `Optional<User> UserService.authenticate(String email, String raw)` | Возвращает пользователя при совпадении BCrypt-хеша, иначе `empty`. |
| Удаление по ISBN | `void BookService.deleteByIsbn(String isbn)` | Найдена книга по ISBN → удаление; иначе 404. |

## 4. Применённые паттерны проектирования

| Паттерн | Где |
|---------|-----|
| Data Mapper | Spring Data репозитории (Entity ↔ хранилище) |
| Identity Map | Контекст персистентности Hibernate |
| Dependency Injection | Конструкторное внедрение Spring |
| Strategy | `PasswordEncoder` (BCrypt) как сменная стратегия хеширования |
| Interceptor / Filter | `AuthInterceptor` (кросс-каттинг авторизация) |
| DTO | `*Request`, `UserResponse`, `PageResponse`, `ErrorResponse` |
