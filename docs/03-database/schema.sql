-- Физическая модель данных (PostgreSQL) для системы «Электронная библиотека»
-- Книги хранятся в Elasticsearch (индекс books), здесь — реляционные данные.

-- =========================================================
-- Пользователи
-- =========================================================
CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL    PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,            -- BCrypt-хеш, не открытый пароль
    name          VARCHAR(255) NOT NULL,
    role          VARCHAR(32)  NOT NULL DEFAULT 'USER'
                  CHECK (role IN ('USER', 'LIBRARIAN')),
    created_at    TIMESTAMP    NOT NULL DEFAULT now()
);

-- =========================================================
-- Выдачи (история «кто и когда брал книгу»)
-- book_id ссылается на Book.id в Elasticsearch (межхранилищная связь, не FK)
-- =========================================================
CREATE TABLE IF NOT EXISTS loans (
    id          BIGSERIAL    PRIMARY KEY,
    book_id     VARCHAR(255) NOT NULL,
    user_id     BIGINT       NOT NULL REFERENCES users(id),
    borrowed_at TIMESTAMP    NOT NULL DEFAULT now(),
    returned_at TIMESTAMP                            -- NULL = активная (невозвращённая) выдача
);

-- Индексы под частые запросы
CREATE INDEX IF NOT EXISTS idx_loans_book_active
    ON loans(book_id) WHERE returned_at IS NULL;     -- активная выдача по книге
CREATE INDEX IF NOT EXISTS idx_loans_user_active
    ON loans(user_id) WHERE returned_at IS NULL;     -- книги на руках у пользователя
