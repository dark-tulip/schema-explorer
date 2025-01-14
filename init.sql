-- Выполняется единожды при запуске конетейнера в докер композере

-- 1. Source database (тестовый пример источника БД откуда берут данные)
CREATE DATABASE source_db;
CREATE USER user1 WITH PASSWORD 'user1pwd';
ALTER DATABASE source_db OWNER TO user1;

-- 2. Sink database (тестовый пример источника БД куда кладут данные)
CREATE DATABASE sink_db;
CREATE USER user2 WITH PASSWORD 'user2pwd';
ALTER DATABASE sink_db OWNER TO user2;
