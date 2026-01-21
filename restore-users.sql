-- Restaurar usuarios en la base de datos de producción
-- Ejecutar con: docker exec -i postgres_db psql -U postgres -d lobbysync < restore-users.sql

-- Insertar usuarios
INSERT INTO users (id, created_at, email, firebase_uid, first_name, is_active, last_name, role, unit_id) 
VALUES 
(1, '2026-01-15 23:18:47.083', 'concierge@lobbysync.com', 'pzggB79v2uWt5i8kftQEhhBJxeS2', '', true, '', 'CONCIERGE', NULL),
(2, '2026-01-16 00:29:45.858', 'residente1@test.com', 'test-resident-101', 'Juan Carlos', true, 'Perez', 'RESIDENT', 1),
(3, '2026-01-16 00:34:04.294', 'resident@lobbysync.com', 'aNLPs5KZguWVrhqP3dl2b5VeHDj1', 'Maria', true, 'Rodriguez', 'RESIDENT', 1),
(4, '2026-01-16 00:34:04.305', 'resident2@lobbysync.com', 'firebase-resident-102', 'Roberto', true, 'Martinez', 'RESIDENT', 2),
(5, '2026-01-16 00:34:04.316', 'resident3@lobbysync.com', 'firebase-resident-103', 'Pedro', true, 'Gonzalez', 'RESIDENT', 3)
ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    firebase_uid = EXCLUDED.firebase_uid,
    first_name = EXCLUDED.first_name,
    is_active = EXCLUDED.is_active,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    unit_id = EXCLUDED.unit_id;

-- Actualizar la secuencia para que el próximo ID sea correcto
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
