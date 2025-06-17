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
END $$;