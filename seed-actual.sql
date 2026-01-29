-- Script de datos para el esquema actual de LobbySync
-- Ejecutar: Get-Content seed-actual.sql | ssh root@168.197.50.14 "docker exec -i postgres_db psql -U postgres -d lobbysync"

-- 1. BUILDINGS
INSERT INTO buildings (id, name, address, floors, is_active) VALUES
(1, 'Torre Norte', 'Av. Principal 123', 15, true),
(2, 'Torre Sur', 'Av. Principal 125', 12, true),
(3, 'Edificio Central', 'Calle Central 456', 10, true)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    address = EXCLUDED.address,
    floors = EXCLUDED.floors,
    is_active = EXCLUDED.is_active;

-- 2. UNITS
INSERT INTO units (id, building_id, floor, unit_number, aliquot, is_active) VALUES
-- Torre Norte
(1, 1, '1', '101', 8.33, true),
(2, 1, '1', '102', 8.33, true),
(3, 1, '2', '201', 8.33, true),
(4, 1, '2', '202', 8.33, true),
(5, 1, '3', '301', 8.33, true),
(6, 1, '3', '302', 8.33, true),
-- Torre Sur
(7, 2, '1', '101', 12.50, true),
(8, 2, '1', '102', 12.50, true),
(9, 2, '2', '201', 12.50, true),
(10, 2, '2', '202', 12.50, true),
-- Edificio Central
(11, 3, '1', '101', 16.67, true),
(12, 3, '1', '102', 16.67, true)
ON CONFLICT (id) DO UPDATE SET
    building_id = EXCLUDED.building_id,
    floor = EXCLUDED.floor,
    unit_number = EXCLUDED.unit_number,
    aliquot = EXCLUDED.aliquot,
    is_active = EXCLUDED.is_active;

-- 3. COMMON AREAS
INSERT INTO common_areas (id, building_id, name, description, capacity, hourly_rate, requires_approval, is_active, created_at) VALUES
(1, 1, 'Salón de Eventos', 'Salón principal para eventos', 100, 50000.00, true, true, NOW()),
(2, 1, 'Quincho', 'Área de parrilla', 30, 25000.00, false, true, NOW()),
(3, 1, 'Piscina', 'Piscina temperada', 50, 0.00, false, true, NOW()),
(4, 1, 'Gimnasio', 'Sala de ejercicios', 20, 0.00, false, true, NOW()),
(5, 2, 'Salón de Juegos', 'Sala recreativa', 15, 15000.00, false, true, NOW())
ON CONFLICT (id) DO UPDATE SET
    building_id = EXCLUDED.building_id,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    capacity = EXCLUDED.capacity,
    hourly_rate = EXCLUDED.hourly_rate,
    requires_approval = EXCLUDED.requires_approval,
    is_active = EXCLUDED.is_active;

-- 4. USERS
INSERT INTO users (id, email, firebase_uid, first_name, last_name, phone, role, is_active, created_at) VALUES
-- Administradores
(1, 'admin@lobbysync.cl', 'firebase_admin_1', 'Admin', 'Principal', '+56912345678', 'ADMIN', true, NOW()),
(2, 'admin2@lobbysync.cl', 'firebase_admin_2', 'Admin', 'Secundario', '+56912345679', 'ADMIN', true, NOW()),
-- Conserjes
(3, 'conserje1@lobbysync.cl', 'firebase_conserje_1', 'Juan', 'Pérez', '+56987654321', 'CONCIERGE', true, NOW()),
(4, 'conserje2@lobbysync.cl', 'firebase_conserje_2', 'María', 'González', '+56987654322', 'CONCIERGE', true, NOW()),
-- Residentes Torre Norte
(5, 'residente1@mail.com', 'firebase_res_1', 'Carlos', 'Rodríguez', '+56911111111', 'RESIDENT', true, NOW()),
(6, 'residente2@mail.com', 'firebase_res_2', 'Ana', 'Martínez', '+56922222222', 'RESIDENT', true, NOW()),
(7, 'residente3@mail.com', 'firebase_res_3', 'Pedro', 'López', '+56933333333', 'RESIDENT', true, NOW()),
-- Residentes Torre Sur
(8, 'residente4@mail.com', 'firebase_res_4', 'Laura', 'Silva', '+56944444444', 'RESIDENT', true, NOW()),
(9, 'residente5@mail.com', 'firebase_res_5', 'Diego', 'Muñoz', '+56955555555', 'RESIDENT', true, NOW())
ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    firebase_uid = EXCLUDED.firebase_uid,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    phone = EXCLUDED.phone,
    role = EXCLUDED.role,
    is_active = EXCLUDED.is_active;

-- Resetear secuencias
SELECT setval('buildings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM buildings));
SELECT setval('units_id_seq', (SELECT COALESCE(MAX(id), 1) FROM units));
SELECT setval('common_areas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM common_areas));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));

-- Verificar datos
SELECT 'Buildings' as tabla, COUNT(*) as registros FROM buildings
UNION ALL
SELECT 'Units', COUNT(*) FROM units
UNION ALL
SELECT 'Common Areas', COUNT(*) FROM common_areas
UNION ALL
SELECT 'Users', COUNT(*) FROM users
UNION ALL
SELECT 'Reservations', COUNT(*) FROM reservations
UNION ALL
SELECT 'Invitations', COUNT(*) FROM invitations;
