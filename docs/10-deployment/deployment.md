# Этап 10. Развёртывание и эксплуатация

## 1. Контейнеризация (Docker)

Инфраструктура (PostgreSQL + Elasticsearch) поднимается через `docker-compose.yml` в корне проекта:

```bash
docker compose up -d        # поднять PostgreSQL (5432) и Elasticsearch (9200)
docker compose ps           # статус
docker compose down         # остановить
docker compose down -v      # остановить и удалить тома (сброс данных)
```

| Сервис | Образ | Порт |
|--------|-------|------|
| PostgreSQL | `postgres:16-alpine` | 5432 |
| Elasticsearch | `elasticsearch:8.11.1` | 9200 |

## 2. Запуск приложения

```bash
# Бэкенд
cd library-backend
mvn spring-boot:run          # http://localhost:8080

# Фронтенд
cd ../library-frontend
npm install
npm run dev                  # http://localhost:5173
```

Сборка артефактов:
```bash
mvn clean package            # library-backend/target/project-0.0.1-SNAPSHOT.jar
npm run build                # library-frontend/dist/
```

## 3. Конфигурация (application.properties)

| Свойство | Назначение |
|----------|------------|
| `spring.datasource.*` | Подключение к PostgreSQL |
| `spring.elasticsearch.uris` | Подключение к Elasticsearch |
| `app.cors.allowed-origin` | Источник фронтенда для CORS |
| `app.admin.*` | Стартовая учётка библиотекаря (email/пароль/имя, вкл/выкл) |
| `app.books.seed-enabled` | Загрузка демо-каталога при пустом индексе |

> Для продакшена пароль стартового библиотекаря и доступы к БД рекомендуется выносить в
> переменные окружения, а `app.admin.enabled=false` после создания учёток.

## 4. Требования к окружению
Java 21+, Node.js 18+, Docker 20+ (или локальные PostgreSQL 16 и Elasticsearch 8.11).

## 5. Пересоздание индекса книг
При изменении маппинга полей (анализаторы) индекс ES нужно пересоздать:
```bash
curl -X DELETE http://localhost:9200/books    # или docker compose down -v
```
При следующем старте Spring Data создаст индекс с актуальным маппингом, а сидер заполнит каталог.

## 6. Примечание по варианту развёртывания
Методичка для веб-траектории предлагает сборку WAR и Tomcat; в проекте использован встроенный
сервер Spring Boot + контейнеризация зависимостей через Docker (бонус веб-траектории). При
необходимости приложение упаковывается в WAR и разворачивается на внешнем Tomcat без изменения
бизнес-логики.
