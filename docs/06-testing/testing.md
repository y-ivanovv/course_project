# Этап 6. Тестирование и обеспечение качества

## 1. Стратегия тестирования

| Уровень | Инструмент | Что проверяется |
|---------|-----------|------------------|
| Модульное | JUnit 5 + Mockito | Бизнес-логика сервисов (изоляция от БД через моки) |
| Покрытие | JaCoCo | Доля покрытой бизнес-логики |
| Контекст | Spring Boot Test | Поднятие контекста (нужен Docker: PG + ES) |
| Качество фронтенда | ESLint | Статанализ React-кода |

## 2. Модульные тесты (17 шт.)

| Класс | Тесты |
|-------|-------|
| `BookServiceImplTest` (5) | создание книги; дубль ISBN; удаление несуществующей; удаление по ISBN (успех/не найдено) |
| `LoanServiceImplTest` (5) | выдача; повторная выдача (409); возврат; **возврат чужим пользователем (403)**; возврат без активной выдачи (404) |
| `UserServiceImplTest` (7) | хеширование пароля; роль LIBRARIAN; дубль email; аутентификация (успех/неверный пароль/нет пользователя); смена имени несуществующему |

## 3. Запуск тестов

```bash
cd library-backend
# быстрые юнит-тесты (без инфраструктуры)
mvn test "-Dtest=BookServiceImplTest,UserServiceImplTest,LoanServiceImplTest"
# все тесты (нужен docker compose up -d)
mvn test
```

Ожидаемый результат юнит-тестов:
```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 4. Отчёт о покрытии (JaCoCo)

Формируется автоматически после `mvn test`:
```
library-backend/target/site/jacoco/index.html
```
Скриншот отчёта добавляется в `docs/06-testing/jacoco-report/`. Бизнес-логика сервисов
(`mediator.services`) покрыта модульными тестами; целевой порог по методичке — **> 40%**.

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
