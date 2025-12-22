DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM users.user
        WHERE login = 'admin'
    ) THEN
        INSERT INTO users.user (login, password, admin)
        VALUES (
            'admin',
            '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',
            TRUE
        );
        RAISE NOTICE 'Добавлен дефолтный администратор (login: admin, password: admin)';
ELSE
        RAISE NOTICE 'Пользователь admin уже существует';
END IF;
END $$ LANGUAGE plpgsql;