# Этап 5. Реализация ядра

## 1. Структура проекта (соответствие PCMEF)

```
library-backend/src/main/java/ru/edu/project/
├── ProjectApplication.java
├── control/                      # Слой Control
│   ├── controllers/              #   BookController, UserController
│   ├── dto/                      #   *Request, UserResponse, PageResponse, ErrorResponse
│   └── advice/                   #   GlobalExceptionHandler
├── mediator/                     # Слой Mediator
│   ├── interfaces/               #   BookService, UserService, LoanService
│   ├── services/                 #   *ServiceImpl
│   └── exceptions/               #   ResourceNotFound/Duplicate/ForbiddenOperation
├── entity/                       # Слой Entity: Book, User, Loan
└── foundation/                   # Слой Foundation
    ├── repositories/             #   BookRepository, UserRepository, LoanRepository
    ├── security/                 #   AuthInterceptor, RequireRole
    └── config/                   #   SecurityConfig, WebConfig, DataInitializer, BookDataInitializer
library-frontend/src/             # Слой Presentation (React SPA)
```

## 2. Классы-сущности (не анемичные)

Бизнес-методы инкапсулированы в сущностях:
- `Book.borrowBook()` / `returnBook()` / `isAvailable()` — инвариант статуса;
- `Loan.markReturned()` / `isActive()` — состояние выдачи;
- `User.changeName()` — валидация имени.

## 3. Слой доступа к данным (Foundation)

- `BookRepository extends ElasticsearchRepository<Book,String>` — поиск `searchByQuery`, `findByIsbn`.
- `UserRepository extends JpaRepository<User,Long>` — `findByEmail`.
- `LoanRepository extends JpaRepository<Loan,Long>` — `findByBookIdAndReturnedAtIsNull`, `findByUserIdAndReturnedAtIsNull`.

## 4. Слой управления (Mediator)

- `BookServiceImpl` — CRUD + поиск + пагинация.
- `UserServiceImpl` — регистрация (USER/LIBRARIAN), аутентификация (BCrypt).
- `LoanServiceImpl` — выдача/возврат (`@Transactional`), проверка владельца выдачи.

## 5. Инициализация данных

- `DataInitializer` — создаёт стартового библиотекаря из `app.admin.*`, если его нет.
- `BookDataInitializer` — загружает ~105 демо-книг из `books-seed.json`, если индекс пуст.

## 6. Объём кода

Соответствует ориентиру веб-траектории (~3000 строк): серверное ядро (контроллеры, сервисы,
сущности, репозитории, конфигурация) + React SPA (страницы, контекст, хук, стили).
