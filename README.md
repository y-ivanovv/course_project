# Электронная библиотека (Library)

**Автор:** Иванов Юрий Сергеевич
**Группа:** ПИЖ-б-о-23-1
**Направление:** 09.03.04 «Программная инженерия», профиль «Разработка и сопровождение ПО»
**Траектория:** Б — Веб-ориентированная (React + Spring Boot)
**Дисциплина:** Программная инженерия (курсовой проект)

## Описание проекта

**Электронная библиотека** — клиент-серверное веб-приложение для автоматизации работы библиотеки.
Система ведёт каталог книг, регистрирует читателей и библиотекарей, обеспечивает полнотекстовый
поиск книг (с учётом морфологии русского языка) и учёт выдачи/возврата изданий с привязкой к
конкретному пользователю.

## Траектория выполнения

- [x] **Б — Веб-разработка** (React + Spring Boot + PostgreSQL)
- [ ] А — Десктоп
- [ ] В — Мобильная
- [ ] Г — Enterprise

## Технологический стек

| Компонент        | Технология                                              |
|------------------|---------------------------------------------------------|
| Бэкенд           | Java 21, Spring Boot 3.4.2                               |
| Архитектура      | PCMEF (Presentation · Control · Mediator · Entity · Foundation) |
| Реляционная БД   | PostgreSQL 16 (пользователи, выдачи) — Spring Data JPA / Hibernate |
| Поиск (NoSQL)    | Elasticsearch 8.11 (каталог книг) — Spring Data Elasticsearch |
| Фронтенд         | React 19, Vite, React Router, React Hook Form, Axios    |
| API              | REST, OpenAPI/Swagger (springdoc)                       |
| Безопасность     | Spring Security, BCrypt, сессии, роли USER/LIBRARIAN     |
| Тестирование     | JUnit 5, Mockito, JaCoCo                                 |
| Контейнеризация  | Docker, Docker Compose                                   |
| Инструменты      | Git, Maven, ESLint                                       |

## Требования к окружению

| Требование | Версия |
|------------|--------|
| Java JDK   | 21+    |
| Node.js    | 18+    |
| PostgreSQL | 16     |
| Elasticsearch | 8.11 |
| Maven      | 3.8+   |
| Docker     | 20+    |

## Установка и запуск

```bash
# 1. Поднять инфраструктуру (PostgreSQL + Elasticsearch)
docker compose up -d

# 2. Запустить бэкенд (из library-backend)
cd library-backend
mvn spring-boot:run          # http://localhost:8080 ; Swagger UI: /swagger-ui.html

# 3. Запустить фронтенд (из library-frontend)
cd ../library-frontend
npm install
npm run dev                  # http://localhost:5173
```

При первом запуске бэкенд автоматически:
- создаёт стартового библиотекаря (настройки `app.admin.*` в `application.properties`);
- заполняет каталог демо-книгами (~105 шт.) из `books-seed.json`, если индекс пуст.

Подробные инструкции — в [docs/10-deployment](docs/10-deployment/deployment.md) и
[docs/11-user-guide](docs/11-user-guide/user-guide.md).

## Тестирование

```bash
cd library-backend
mvn test "-Dtest=BookServiceImplTest,UserServiceImplTest,LoanServiceImplTest"   # быстрые юнит-тесты
mvn test                                                                          # все тесты (нужен Docker)
```
Отчёт о покрытии (JaCoCo): `library-backend/target/site/jacoco/index.html`.

## Структура репозитория

```
library-project/
├── library-backend/     # Spring Boot: слои Control, Mediator, Entity, Foundation
├── library-frontend/    # React SPA: слой Presentation
├── docker-compose.yml   # PostgreSQL + Elasticsearch
└── docs/                # Проектная документация по этапам
    ├── 00-project-charter/   # Паспорт проекта, бизнес-анализ
    ├── 01-requirements/      # Требования, Use Case, Domain Model
    ├── 02-architecture/      # PCMEF, ADR, интерфейсы
    ├── 03-database/          # ER-модель, DDL, ORM
    ├── 04-detailed-design/   # Диаграммы последовательности и классов
    ├── 05-implementation/    # Описание реализации
    ├── 06-testing/           # Тестирование и покрытие
    ├── 07-refactoring/       # Рефакторинг, паттерны, статанализ
    ├── 08-ui/                # Пользовательский интерфейс
    ├── 09-api/               # Спецификация REST API
    ├── 10-deployment/        # Развёртывание (Docker)
    ├── 11-user-guide/        # Руководства пользователя и администратора
    └── 12-final-report/      # Пояснительная записка
```

## Документация по этапам

| Этап | Документ |
|------|----------|
| 0. Инициация и бизнес-анализ | [docs/00-project-charter](docs/00-project-charter/project-charter.md) |
| 1. Требования | [docs/01-requirements](docs/01-requirements/requirements.md) |
| 2. Архитектура (PCMEF) | [docs/02-architecture](docs/02-architecture/architecture.md) |
| 3. База данных | [docs/03-database](docs/03-database/database.md) |
| 4. Детальное проектирование | [docs/04-detailed-design](docs/04-detailed-design/detailed-design.md) |
| 5. Реализация ядра | [docs/05-implementation](docs/05-implementation/implementation.md) |
| 6. Тестирование | [docs/06-testing](docs/06-testing/testing.md) |
| 7. Рефакторинг | [docs/07-refactoring](docs/07-refactoring/refactoring.md) |
| 8. Интерфейс | [docs/08-ui](docs/08-ui/ui.md) |
| 9. REST API | [docs/09-api](docs/09-api/api.md) |
| 10. Развёртывание | [docs/10-deployment](docs/10-deployment/deployment.md) |
| 11. Руководство пользователя | [docs/11-user-guide](docs/11-user-guide/user-guide.md) |
| 12. Пояснительная записка | [docs/12-final-report](docs/12-final-report/explanatory-note.md) |

## Статистика разработки

### Метрики Git
- Всего коммитов: 9
- Период активной разработки (история Git): 04.06.2026 — 05.06.2026
- Ветвление: разработка велась в ветке `refactor`, влита в `master`

> Примечание: репозиторий Git был инициализирован на этапе рефакторинга, поэтому история
> отражает финальную фазу работы. Скриншоты GitHub Insights (Commit Activity, Punch Card)
> добавляются в `docs/images/git-stats-*.png` после публикации репозитория на GitHub:
>
> ```markdown
> ![Активность коммитов](docs/images/git-commit-activity.png)
> ![Распределение по времени](docs/images/git-punch-card.png)
> ```

## Лицензия

Учебный проект. Свободное использование в образовательных целях.
