DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'capability' AND table_name = 'tech_capability'
        ) THEN
            CREATE TABLE capability.tech_capability (
                                                        id SERIAL PRIMARY KEY,
                                                        code TEXT UNIQUE NOT NULL,
                                                        name TEXT NOT NULL,
                                                        description TEXT,
                                                        created_date TIMESTAMP WITHOUT TIME ZONE,
                                                        last_modified_date TIMESTAMP WITHOUT TIME ZONE,
                                                        deleted_date TIMESTAMP WITHOUT TIME ZONE,
                                                        status TEXT,
                                                        responsibility_product_id INTEGER REFERENCES products.product(id)
            );
            RAISE NOTICE 'Таблица capability.tech_capability создана';
        ELSE
            RAISE NOTICE 'Таблица capability.tech_capability уже существует';
        END IF;
    END;
$$ LANGUAGE plpgsql;
