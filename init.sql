-- Выполняется единожды при запуске конетейнера в докер композере

-- 1. Source database (тестовый пример источника БД откуда берут данные)
CREATE DATABASE source_db;
CREATE USER user1 WITH PASSWORD 'user1pwd';
ALTER DATABASE source_db OWNER TO user1;
GRANT ALL PRIVILEGES ON DATABASE source_db TO user1;
ALTER USER user1 WITH SUPERUSER; -- чтобы мочь апдейтить системные параметры

-- 2. Sink database (тестовый пример источника БД куда кладут данные)
CREATE DATABASE sink_db;
CREATE USER user2 WITH PASSWORD 'user2pwd';
ALTER DATABASE sink_db OWNER TO user2;
GRANT ALL PRIVILEGES ON DATABASE sink_db TO user2;
ALTER USER user2 WITH SUPERUSER;


-- =========================== 1.1 Подключение к source_db ===============================
\c source_db user1

-- Создание таблицы и вставка данных
CREATE TABLE books
(
    id             BIGSERIAL PRIMARY KEY,
    title          text NOT NULL,
    author         text NOT NULL,
    published_year INTEGER
);

INSERT INTO books (title, author, published_year)
VALUES ('1984', 'George Orwell', 1949),
       ('To Kill a Mockingbird', 'Harper Lee', 1960),
       ('The Great Gatsby', 'F. Scott Fitzgerald', 1925),
       ('Pride and Prejudice', 'Jane Austen', 1813),
       ('The Catcher in the Rye', 'J.D. Salinger', 1951);

-- Установить wal_level на уровне базы данных
ALTER SYSTEM SET wal_level = 'logical';

-- Перезагрузить конфигурацию для применения изменений
SELECT pg_reload_conf();

-- ======================================================================================

-- =========================== 1.2 Подключение к sink_db ===============================
\c sink_db user2

-- Создание таблицы и вставка данных
CREATE TABLE books
(
    id             BIGSERIAL PRIMARY KEY,
    title          text NOT NULL,
    author         text NOT NULL,
    published_year INTEGER
);
-- ======================================================================================
