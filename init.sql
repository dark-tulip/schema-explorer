-- Выполняется единожды при запуске конетейнера в докер композере

-- 1. Source database (тестовый пример источника БД откуда берут данные)
CREATE DATABASE source_db;
CREATE USER user1 WITH PASSWORD 'user1pwd';
ALTER DATABASE source_db OWNER TO user1;

-- 2. Sink database (тестовый пример источника БД куда кладут данные)
CREATE DATABASE sink_db;
CREATE USER user2 WITH PASSWORD 'user2pwd';
ALTER DATABASE sink_db OWNER TO user2;

-- =========================== 1.1 Подключение к source_db ===============================
\connect source_db;

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

-- ======================================================================================
