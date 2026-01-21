-- Script simplificado de datos de prueba para LobbySync
-- Solo inserta Buildings, Units y Users

-- 1. BUILDINGS (solo columnas: name, address, floors, is_active)
INSERT INTO buildings (id, name, address, floors, is_active) VALUES
(1, 'Torre Norte', 'Av. Principal 123', 15, true),
(2, 'Torre Sur', 'Av. Principal 125', 12, true),
(3, 'Edificio Central', 'Calle Central 456', 10, true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    address = EXCLUDED.address,
    floors = EXCLUDED.floors,
    is_active = EXCLUDED.is_active;

-- 2. UNITS (solo columnas: unit_number, building_id, aliquot, is_active)
INSERT INTO units (id, unit_number, building_id, aliquot, is_active) VALUES
-- Torre Norte
(1, '101', 1, 2.50, true),
(2, '102', 1, 2.50, true),
(3, '103', 1, 2.50, true),
(4, '104', 1, 2.50, true),
(5, '201', 1, 2.50, true),
(6, '202', 1, 2.50, true),
(7, '203', 1, 2.50, true),
(8, '204', 1, 2.50, true),
-- Torre Sur
(11, '101', 2, 3.00, true),
(12, '102', 2, 3.00, true),
(13, '201', 2, 3.00, true),
(14, '202', 2, 3.00, true)
ON CONFLICT (id) DO UPDATE SET
    unit_number = EXCLUDED.unit_number,
    building_id = EXCLUDED.building_id,
    aliquot = EXCLUDED.aliquot,
    is_active = EXCLUDED.is_active;

-- 3. COMMON AREAS (con created_at)
INSERT INTO common_areas (id, name, description, capacity, hourly_rate, is_active, building_id, requires_approval, created_at) VALUES
(1, 'Salón de Eventos', 'Salón principal para eventos', 100, 50000.00, true, 1, true, NOW()),
(2, 'Quincho', 'Área de parrilla', 30, 25000.00, true, 1, false, NOW()),
(3, 'Piscina', 'Piscina temperada', 50, 0.00, true, 1, false, NOW()),
(4, 'Gimnasio', 'Sala de ejercicios', 20, 0.00, true, 1, false, NOW())
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    capacity = EXCLUDED.capacity,
    hourly_rate = EXCLUDED.hourly_rate,
    is_active = EXCLUDED.is_active,
    building_id = EXCLUDED.building_id,
    requires_approval = EXCLUDED.requires_approval;

-- 4. USERS (con created_at)
-- Conserje sin unit_id
INSERT INTO users (id, email, firebase_uid, first_name, last_name, role, is_active, unit_id, created_at) VALUES
(1, 'concierge@lobbysync.com', 'pzggB79v2uWt5i8kftQEhhBJxeS2', 'Carlos', 'Gomez', 'CONCIERGE', true, NULL, NOW())
ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    firebase_uid = EXCLUDED.firebase_uid,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active;

-- Residentes originales
INSERT INTO users (id, email, firebase_uid, first_name, last_name, role, is_active, unit_id, created_at) VALUES
(2, 'residente1@test.com', 'test-resident-101', 'Juan Carlos', 'Perez', 'RESIDENT', true, 1, NOW()),
(3, 'resident@lobbysync.com', 'aNLPs5KZguWVrhqP3dl2b5VeHDj1', 'Maria', 'Rodriguez', 'RESIDENT', true, 1, NOW()),
(4, 'resident2@lobbysync.com', 'firebase-resident-102', 'Roberto', 'Martinez', 'RESIDENT', true, 2, NOW()),
(5, 'resident3@lobbysync.com', 'firebase-resident-103', 'Pedro', 'Gonzalez', 'RESIDENT', true, 3, NOW())
ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    firebase_uid = EXCLUDED.firebase_uid,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active,
    unit_id = EXCLUDED.unit_id;

-- Residentes adicionales
INSERT INTO users (id, email, firebase_uid, first_name, last_name, role, is_active, unit_id, created_at) VALUES
(6, 'resident4@test.com', 'test-resident-104', 'Ana', 'Silva', 'RESIDENT', true, 5, NOW()),
(7, 'resident5@test.com', 'test-resident-105', 'Luis', 'Fernandez', 'RESIDENT', true, 6, NOW()),
(8, 'admin@lobbysync.com', 'admin-firebase-uid', 'Admin', 'Sistema', 'ADMIN', true, NULL, NOW())
ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    firebase_uid = EXCLUDED.firebase_uid,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active,
    unit_id = EXCLUDED.unit_id;

-- Actualizar secuencias
SELECT setval('buildings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM buildings));
SELECT setval('units_id_seq', (SELECT COALESCE(MAX(id), 1) FROM units));
SELECT setval('common_areas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM common_areas));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));

-- Resumen
SELECT 'Buildings' as tabla, COUNT(*) as registros FROM buildings
UNION ALL
SELECT 'Units', COUNT(*) FROM units
UNION ALL
SELECT 'Common Areas', COUNT(*) FROM common_areas
UNION ALL  
SELECT 'Users', COUNT(*) FROM users;
