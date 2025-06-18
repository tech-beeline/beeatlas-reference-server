DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.schemata WHERE schema_name = 'capability'
    ) THEN
CREATE SCHEMA capability;
RAISE NOTICE 'Схема capability создана';
ELSE
        RAISE NOTICE 'Схема capability уже существует';
END IF;
END $$ LANGUAGE plpgsql;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'capability' AND table_name = 'business_capability'
    ) THEN
CREATE TABLE capability.business_capability (
                                                id SERIAL PRIMARY KEY,
                                                code TEXT UNIQUE NOT NULL,
                                                name TEXT NOT NULL,
                                                description TEXT,
                                                created_date TIMESTAMP WITHOUT TIME ZONE,
                                                last_modified_date TIMESTAMP WITHOUT TIME ZONE,
                                                deleted_date TIMESTAMP WITHOUT TIME ZONE,
                                                status TEXT,
                                                parent_id INTEGER REFERENCES capability.business_capability(id),
                                                is_domain BOOLEAN
);
RAISE NOTICE 'Таблица capability.business_capability создана';
ELSE
        RAISE NOTICE 'Таблица capability.business_capability уже существует';
END IF;
END $$ LANGUAGE plpgsql;
