# Этап 9. REST API

Базовый URL: `http://localhost:8080/api`. Формат — JSON. Аутентификация — серверная сессия
(cookie). Интерактивная документация — **Swagger UI**: `http://localhost:8080/swagger-ui.html`
(спецификация OpenAPI: `/v3/api-docs`).

## 1. Эндпоинты (13)

### Пользователи
| Метод | Путь | Описание | Доступ | Тело/ответ |
|-------|------|----------|--------|------------|
| POST | `/api/users/register` | Регистрация читателя | Публичный | `{email,password,name}` → `UserResponse` |
| POST | `/api/users/login` | Вход (создаёт сессию) | Публичный | `{email,password}` → `UserResponse` / 401 |
| POST | `/api/users/logout` | Выход | Авторизован | — |
| POST | `/api/users/librarians` | Создать библиотекаря | LIBRARIAN | `{email,password,name}` → `UserResponse` |
| GET | `/api/users/{id}` | Пользователь по id | Авторизован | `UserResponse` / 404 |

### Книги и каталог
| Метод | Путь | Описание | Доступ | Тело/ответ |
|-------|------|----------|--------|------------|
| GET | `/api/books?offset=&limit=` | Список с пагинацией | Авторизован | `PageResponse<Book>` |
| GET | `/api/books/{id}` | Книга по id | Авторизован | `Book` / 404 |
| POST | `/api/books/search?offset=&limit=` | Полнотекстовый поиск | Авторизован | `{query}` → `PageResponse<Book>` |
| POST | `/api/books` | Добавить книгу | LIBRARIAN | `BookCreateRequest` → `Book` |
| DELETE | `/api/books/{id}` | Удалить по id | LIBRARIAN | 204 |
| DELETE | `/api/books/by-isbn/{isbn}` | Удалить по ISBN | LIBRARIAN | 204 / 404 |
| POST | `/api/books/{id}/borrow` | Взять книгу | Авторизован | `Loan` / 409 |
| POST | `/api/books/{id}/return` | Вернуть книгу | Владелец выдачи | `Loan` / 403 / 404 |

## 2. Коды состояния

| Код | Когда |
|-----|-------|
| 200 / 204 | Успех |
| 400 | Ошибка валидации (`ErrorResponse` с `fieldErrors`) |
| 401 | Нет сессии |
| 403 | Недостаточно прав / возврат чужой книги |
| 404 | Ресурс не найден |
| 409 | Конфликт (дубль ISBN/email, книга уже выдана) |

## 3. Формат ошибки

```json
{
  "timestamp": "2026-06-05T12:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Книга с таким ISBN уже существует!",
  "fieldErrors": null
}
```

## 4. Пагинированный ответ

```json
{ "content": [ /* Book[] */ ], "total": 105, "offset": 0, "limit": 12 }
```

## 5. Соответствие требованиям
- Эндпоинтов: **13** (требование веб-траектории — ≥ 8). ✅
- Пагинация и фильтрация (поиск) для списков. ✅
- Документация OpenAPI (Swagger UI). ✅
- Стандартные HTTP-статусы и валидация входных данных. ✅
