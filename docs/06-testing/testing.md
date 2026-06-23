# Этап 6. Тестирование и обеспечение качества

## 1. Стратегия тестирования

| Уровень | Инструмент | Что проверяется |
|---------|-----------|------------------|
| Модульное | JUnit 5 + Mockito | Бизнес-логика сервисов (изоляция от БД через моки) |
| Покрытие | JaCoCo | Доля покрытой бизнес-логики |
| Контекст | Spring Boot Test | Поднятие контекста (нужен Docker: PG + ES) |
| Качество фронтенда | ESLint | Статанализ React-кода |

## 2. Модульные тесты (32 шт.)

| Класс | Тесты |
|-------|-------|
| `BookServiceImplTest` (5) | создание книги; дубль ISBN; удаление несуществующей; удаление по ISBN (успех/не найдено) |
| `LoanServiceImplTest` (5) | выдача; повторная выдача (409); возврат; **возврат чужим пользователем (403)**; возврат без активной выдачи (404) |
| `UserServiceImplTest` (7) | хеширование пароля; роль LIBRARIAN; дубль email; аутентификация (успех/неверный пароль/нет пользователя); смена имени несуществующему |
| `GlobalExceptionHandlerTest` (7) | маппинг исключений в коды 404/409/403/400/500 + ошибки валидации |
| `AuthInterceptorTest` (5) | OPTIONS-preflight; 401 без сессии; 403 при неверной роли; допуск при совпадении роли |
| `DtoTest` (3) | `UserResponse` без хеша пароля; `PageResponse` метаданные; `ErrorResponse` |

## 3. Запуск тестов

```bash
cd library-backend
# быстрые юнит-тесты (без инфраструктуры)
mvn test "-Dtest=BookServiceImplTest,UserServiceImplTest,LoanServiceImplTest,GlobalExceptionHandlerTest,AuthInterceptorTest,DtoTest"
# все тесты (нужен docker compose up -d)
mvn test
```

Ожидаемый результат юнит-тестов:
```
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 4. Отчёт о покрытии (JaCoCo)

Полный HTML-отчёт сохранён в репозитории: [jacoco-report/index.html](jacoco-report/index.html);
сводка с цифрами — [coverage-summary.md](coverage-summary.md). Итоговое покрытие:

| Метрика | Значение |
|---------|----------|
| Инструкции (overall) | **45.7%** |
| Строки (overall) | **47.9%** |
| Ядро `mediator.services` | 79.3% |
| `advice` / `security` / `exceptions` | 100% |

Порог методички (**> 40%**) выполнен.

## 5. Пример теста (ключевой — защита от бага «чужого» возврата)

```java
@Test
void returnBook_ThrowsForbidden_WhenReturnedByDifferentUser() {
    Loan activeLoan = new Loan(BOOK_ID, BORROWER_ID);
    when(loanRepository.findByBookIdAndReturnedAtIsNull(BOOK_ID))
        .thenReturn(Optional.of(activeLoan));

    assertThrows(ForbiddenOperationException.class,
        () -> loanService.returnBook(BOOK_ID, OTHER_USER_ID));

    verify(bookRepository, never()).save(any(Book.class));
    verify(loanRepository, never()).save(any(Loan.class));
}
```
