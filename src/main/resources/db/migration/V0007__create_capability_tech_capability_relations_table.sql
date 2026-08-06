DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'capability' AND table_name = 'tech_capability_relations'
    ) THEN
CREATE TABLE capability.tech_capability_relations (
                                                      id SERIAL PRIMARY KEY,
                                                      parent_id INTEGER REFERENCES capability.business_capability(id),
                                                      child_id INTEGER REFERENCES capability.tech_capability(id)
);
RAISE NOTICE 'Таблица capability.tech_capability_relations создана';
ELSE
        RAISE NOTICE 'Таблица capability.tech_capability_relations уже существует';
END IF;
END $$ LANGUAGE plpgsql;
