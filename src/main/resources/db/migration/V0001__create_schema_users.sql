DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.schemata
        WHERE schema_name = 'users'
    ) THEN
CREATE SCHEMA users;
RAISE NOTICE 'Схема users создана';
ELSE
        RAISE NOTICE 'Схема users уже существует';
END IF;
END $$;DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'users' AND table_name = 'user'
    ) THEN
CREATE TABLE users.user (
                            id SERIAL PRIMARY KEY,
                            login VARCHAR(255) UNIQUE NOT NULL,
                            password VARCHAR(255) NOT NULL,
                            admin BOOLEAN DEFAULT FALSE
);
RAISE NOTICE 'Таблица users.user создана';
ELSE
        RAISE NOTICE 'Таблица users.user уже существует';
END IF;
END $$;