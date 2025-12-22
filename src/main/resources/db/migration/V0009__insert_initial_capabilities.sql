DO
$$
BEGIN
    IF
NOT EXISTS (SELECT 1 FROM capability.business_capability LIMIT 1)
    THEN
       INSERT INTO capability.business_capability (code, name, description, created_date, last_modified_date, deleted_date, status, parent_id, is_domain) VALUES
                                        ('GRP.000', 'Каталог Возможностей (Capability Catalog)', 'Общий каталог возможностей. Включает группы доменов и домены L2 ФДМ.', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', null, true),
                                        ('GRP.001', 'Нефтегазовая Разведка', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 4, true),
                                        ('DMN.001', 'Оценка возможностей', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 5, true),
                                        ('BC.00001', 'Геологическая и геофизическая идентификация и отбор', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 6, false),
                                        ('BC.00002', 'Выявление и развитие возможностей добычи нефти и газа', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 6, false),
                                        ('BC.00003', 'Выявление и развитие перспективных нефтегазовых месторождений', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 6, false),
                                        ('DMN.002', 'Геологический и географический анализ', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 5, true),
                                        ('BC.00004', 'Геопространственный анализ данных', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 10, false),
                                        ('BC.00005', 'Дистанционный анализ', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 10, false),
                                        ('BC.00006', 'Петрофизический анализ', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 10, false),
                                        ('BC.00007', 'Микроскопический и визуальный анализ', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 10, false),
                                        ('BC.00008', 'Характеризация капиллярного давления', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 10, false),
                                        ('BC.00009', 'Геохимический анализ', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 10, false),
                                        ('DMN.003', 'Разведочное бурение', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 5, true),
                                        ('BC.00010', 'Морские буровые операции', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 16, false),
                                        ('BC.00011', 'Наземные буровые операции', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 16, false),
                                        ('BC.00012', 'Управление разрешением на разведку', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 16, false),
                                        ('DMN.004', 'Оценка активов', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 5, true),
                                        ('BC.00013', 'Оценка резервуаров и недр', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 20, false),
                                        ('BC.00014', 'Оценка потенциала производства', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 20, false),
                                        ('BC.00015', 'Экономическое обоснование', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 20, false),
                                        ('BC.00016', 'Техническая и операционная целесообразность', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 20, false),
                                        ('BC.00017', 'Анализ рисков и неопределенностей', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 20, false),
                                        ('BC.00018', 'Оценка рисков экологического и социального воздействия', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 20, false),
                                        ('DMN.005', 'Согласование контрактов', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 5, true),
                                        ('BC.00039', 'Управление контрактами на раздел продукции', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 27, false),
                                        ('BC.00019', 'Управление контрактами на нефтепромысловые услуги и оборудование', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 27, false),
                                        ('BC.00020', 'Hазработка соглашений о совместном предприятии и партнерстве', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 27, false),
                                        ('BC.00021', 'Управление контрактами на продажу и маркетинг углеводородов', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 27, false),
                                        ('BC.00022', 'Администрирование контрактов на бурение', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 27, false),
                                        ('DMN.006', 'Управление венчурным финансированием', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 5, true),
                                        ('BC.00023', 'Cодействие созданию и управлению совместными предприятиями', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 33, false),
                                        ('BC.00024', 'Государственное лицензирование и разрешение', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 33, false),
                                        ('BC.00025', 'Оценка концессионного соглашения', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 33, false),
                                        ('BC.00026', 'Лицензирование и управление разведкой', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 33, false),
                                        ('GRP.002', 'Производство и эксплуатация', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 4, true),
                                        ('DMN.007', 'Управление земельными ресурсами', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 38, true),
                                        ('BC.00027', 'Управление сельскохозяйственными землями', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 39, false),
                                        ('BC.00028', 'Управление лесными землями', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 39, false),
                                        ('BC.00029', 'Управление и согласование аренды земель', '', to_timestamp('09-12-2025 00:00:00','DD-MM-YYYY HH24:MI:SS'), NULL, NULL, 'Proposed', 39, false);
END IF;
END $$;