DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.schemata WHERE schema_name = 'products'
    ) THEN
CREATE SCHEMA products;
RAISE NOTICE 'Схема products создана';
ELSE
        RAISE NOTICE 'Схема products уже существует';
END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'products' AND table_name = 'product'
    ) THEN
CREATE TABLE products.product (
                                  id SERIAL PRIMARY KEY,
                                  name TEXT UNIQUE NOT NULL,
                                  alias TEXT NOT NULL,
                                  description TEXT,
                                  created_date TIMESTAMP WITHOUT TIME ZONE,
                                  last_modified_date TIMESTAMP WITHOUT TIME ZONE,
                                  deleted_date TIMESTAMP WITHOUT TIME ZONE
);
RAISE NOTICE 'Таблица products.product создана';
ELSE
        RAISE NOTICE 'Таблица products.product уже существует';
END IF;
END $$ LANGUAGE plpgsql;
