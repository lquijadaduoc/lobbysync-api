-- Script completo de datos de prueba para LobbySync
-- Ejecutar con: Get-Content seed-production.sql | ssh root@168.197.50.14 "docker exec -i postgres_db psql -U postgres -d lobbysync"

-- Limpiar datos existentes (opcional, comentar si no quieres borrar)
-- TRUNCATE TABLE users, units, buildings, common_areas, reservations, reservation_guests, invitations, vehicles CASCADE;

-- 1. BUILDINGS (Edificios)
INSERT INTO buildings (id, name, address, total_floors, units_per_floor, created_at) VALUES
(1, 'Torre Norte', 'Av. Principal 123', 15, 4, NOW()),
(2, 'Torre Sur', 'Av. Principal 125', 12, 4, NOW()),
(3, 'Edificio Central', 'Calle Central 456', 10, 6, NOW())
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    address = EXCLUDED.address;

-- 2. UNITS (Unidades/Departamentos)
INSERT INTO units (id, building_id, floor, unit_number, created_at) VALUES
-- Torre Norte (Building 1)
(1, 1, 1, '101', NOW()),
(2, 1, 1, '102', NOW()),
(3, 1, 1, '103', NOW()),
(4, 1, 1, '104', NOW()),
(5, 1, 2, '201', NOW()),
(6, 1, 2, '202', NOW()),
(7, 1, 2, '203', NOW()),
(8, 1, 2, '204', NOW()),
(9, 1, 3, '301', NOW()),
(10, 1, 3, '302', NOW()),
-- Torre Sur (Building 2)
(11, 2, 1, '101', NOW()),
(12, 2, 1, '102', NOW()),
(13, 2, 2, '201', NOW()),
(14, 2, 2, '202', NOW()),
-- Edificio Central (Building 3)
(15, 3, 1, '101', NOW()),
(16, 3, 1, '102', NOW()),
(17, 3, 2, '201', NOW()),
(18, 3, 2, '202', NOW())
ON CONFLICT (id) DO UPDATE SET
    building_id = EXCLUDED.building_id,
    floor = EXCLUDED.floor,
    unit_number = EXCLUDED.unit_number;

-- 3. COMMON AREAS (Áreas Comunes)
INSERT INTO common_areas (id, building_id, name, description, capacity, price_per_hour, requires_approval, is_active, created_at) VALUES
(1, 1, 'Salón de Eventos', 'Salón principal para eventos y celebraciones', 100, 50000.00, true, true, NOW()),
(2, 1, 'Quincho', 'Área de parrilla al aire libre', 30, 25000.00, false, true, NOW()),
(3, 1, 'Piscina', 'Piscina temperada', 50, 0.00, false, true, NOW()),
(4, 1, 'Gimnasio', 'Sala de ejercicios con equipamiento', 20, 0.00, false, true, NOW()),
(5, 2, 'Salón de Juegos', 'Sala con mesa de pool y ping pong', 15, 15000.00, false, true, NOW()),
(6, 2, 'Terraza BBQ', 'Terraza en último piso con parrilla', 25, 30000.00, true, true, NOW()),
(7, 3, 'Sala de Reuniones', 'Sala equipada para trabajo remoto', 12, 10000.00, false, true, NOW())
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    capacity = EXCLUDED.capacity,
    price_per_hour = EXCLUDED.price_per_hour;

-- 4. USERS (Usuarios)
-- Primero el conserje (sin unit_id)
INSERT INTO users (id, created_at, email, firebase_uid, first_name, is_active, last_name, role, unit_id) VALUES
(1, '2026-01-15 23:18:47.083', 'concierge@lobbysync.com', 'pzggB79v2uWt5i8kftQEhhBJxeS2', 'Carlos', true, 'Gomez', 'CONCIERGE', NULL)
ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    firebase_uid = EXCLUDED.firebase_uid,
    first_name = EXCLUDED.first_name,
    is_active = EXCLUDED.is_active,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    unit_id = EXCLUDED.unit_id;

-- Residentes originales
INSERT INTO users (id, created_at, email, firebase_uid, first_name, is_active, last_name, role, unit_id) VALUES
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

-- Residentes adicionales de prueba
INSERT INTO users (id, created_at, email, firebase_uid, first_name, is_active, last_name, role, unit_id) VALUES
(6, NOW(), 'resident4@test.com', 'test-resident-104', 'Ana', true, 'Silva', 'RESIDENT', 5),
(7, NOW(), 'resident5@test.com', 'test-resident-105', 'Luis', true, 'Fernandez', 'RESIDENT', 6),
(8, NOW(), 'resident6@test.com', 'test-resident-106', 'Carmen', true, 'Torres', 'RESIDENT', 11),
(9, NOW(), 'resident7@test.com', 'test-resident-107', 'Diego', true, 'Ramirez', 'RESIDENT', 12),
(10, NOW(), 'admin@lobbysync.com', 'admin-firebase-uid', 'Admin', true, 'Sistema', 'ADMIN', NULL)
ON CONFLICT (id) DO UPDATE SET
    email = EXCLUDED.email,
    firebase_uid = EXCLUDED.firebase_uid,
    first_name = EXCLUDED.first_name,
    is_active = EXCLUDED.is_active,
    last_name = EXCLUDED.last_name,
    role = EXCLUDED.role,
    unit_id = EXCLUDED.unit_id;

-- 5. VEHICLES (Vehículos de residentes)
INSERT INTO vehicles (id, user_id, license_plate, brand, model, color, created_at) VALUES
(1, 3, 'ABC123', 'Toyota', 'Corolla', 'Blanco', NOW()),
(2, 4, 'XYZ789', 'Honda', 'Civic', 'Negro', NOW()),
(3, 5, 'DEF456', 'Mazda', 'CX-5', 'Rojo', NOW()),
(4, 6, 'GHI789', 'Nissan', 'Sentra', 'Gris', NOW())
ON CONFLICT (id) DO NOTHING;

-- 6. INVITATIONS (Invitaciones de ejemplo)
INSERT INTO invitations (id, user_id, visitor_name, visitor_rut, visit_date, valid_until, qr_token, status, created_at) VALUES
(1, 3, 'Juan Visitante', '12345678-9', NOW() + INTERVAL '1 day', NOW() + INTERVAL '2 days', 'QR-TOKEN-001', 'PENDING', NOW()),
(2, 4, 'Maria Invitada', '98765432-1', NOW() + INTERVAL '2 days', NOW() + INTERVAL '3 days', 'QR-TOKEN-002', 'PENDING', NOW())
ON CONFLICT (id) DO NOTHING;

-- 7. RESERVATIONS (Reservas de áreas comunes)
INSERT INTO reservations (id, common_area_id, user_id, reservation_date, start_time, end_time, status, total_price, created_at) VALUES
(1, 2, 3, CURRENT_DATE + INTERVAL '3 days', '14:00:00', '18:00:00', 'CONFIRMED', 100000.00, NOW()),
(2, 3, 4, CURRENT_DATE + INTERVAL '5 days', '10:00:00', '12:00:00', 'PENDING', 0.00, NOW()),
(3, 5, 5, CURRENT_DATE + INTERVAL '7 days', '15:00:00', '20:00:00', 'CONFIRMED', 75000.00, NOW())
ON CONFLICT (id) DO NOTHING;

-- 8. RESERVATION GUESTS (Invitados a reservas)
INSERT INTO reservation_guests (id, reservation_id, guest_name, guest_rut) VALUES
(1, 1, 'Carlos Invitado', '11111111-1'),
(2, 1, 'Laura Invitada', '22222222-2'),
(3, 3, 'Pedro Invitado', '33333333-3')
ON CONFLICT (id) DO NOTHING;

-- Actualizar secuencias
SELECT setval('buildings_id_seq', (SELECT COALESCE(MAX(id), 1) FROM buildings));
SELECT setval('units_id_seq', (SELECT COALESCE(MAX(id), 1) FROM units));
SELECT setval('common_areas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM common_areas));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('vehicles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM vehicles));
SELECT setval('invitations_id_seq', (SELECT COALESCE(MAX(id), 1) FROM invitations));
SELECT setval('reservations_id_seq', (SELECT COALESCE(MAX(id), 1) FROM reservations));
SELECT setval('reservation_guests_id_seq', (SELECT COALESCE(MAX(id), 1) FROM reservation_guests));

-- Mostrar resumen
SELECT 'Buildings' as table_name, COUNT(*) as records FROM buildings
UNION ALL
SELECT 'Units', COUNT(*) FROM units
UNION ALL
SELECT 'Common Areas', COUNT(*) FROM common_areas
UNION ALL
SELECT 'Users', COUNT(*) FROM users
UNION ALL
SELECT 'Vehicles', COUNT(*) FROM vehicles
UNION ALL
SELECT 'Invitations', COUNT(*) FROM invitations
UNION ALL
SELECT 'Reservations', COUNT(*) FROM reservations
UNION ALL
SELECT 'Reservation Guests', COUNT(*) FROM reservation_guests;
