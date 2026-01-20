-- ============================================
-- SCRIPT SQL: INSERTAR DATOS DE "MI HOGAR" EN PRODUCCIÓN
-- Tabla: family_members, pets, vehicles
-- ============================================

-- NOTA: Ajusta el unit_id según tus unidades existentes en producción
-- Este script asume que existen units con IDs 1, 2, 3

-- ============================================
-- 1. MIEMBROS DE FAMILIA (family_members)
-- ============================================

-- Unit 1: Familia Pérez-García
INSERT INTO family_members (unit_id, name, rut, relationship, birth_date, phone, email, emergency_contact, created_at, updated_at)
VALUES 
    (1, 'Juan Carlos Pérez López', '12.345.678-9', 'SPOUSE', '1980-05-15', '+56912345678', 'juan.perez@email.com', true, NOW(), NOW()),
    (1, 'María Isabel García Rodríguez', '23.456.789-0', 'SPOUSE', '1982-08-20', '+56923456789', 'maria.garcia@email.com', true, NOW(), NOW()),
    (1, 'Carlos Alberto Pérez García', '25.678.901-2', 'CHILD', '2005-03-10', '+56934567890', NULL, false, NOW(), NOW()),
    (1, 'Sofía Valentina Pérez García', '26.789.012-3', 'CHILD', '2008-11-25', NULL, NULL, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Unit 2: Familia Martínez-Silva
INSERT INTO family_members (unit_id, name, rut, relationship, birth_date, phone, email, emergency_contact, created_at, updated_at)
VALUES 
    (2, 'Roberto Martínez Fuentes', '13.456.789-1', 'SPOUSE', '1978-02-14', '+56945678901', 'roberto.martinez@email.com', true, NOW(), NOW()),
    (2, 'Andrea Silva Campos', '14.567.890-2', 'SPOUSE', '1981-07-30', '+56956789012', 'andrea.silva@email.com', true, NOW(), NOW()),
    (2, 'Diego Martínez Silva', '27.890.123-4', 'CHILD', '2010-09-12', NULL, NULL, false, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Unit 3: Familia González (Persona sola con padres)
INSERT INTO family_members (unit_id, name, rut, relationship, birth_date, phone, email, emergency_contact, created_at, updated_at)
VALUES 
    (3, 'Pedro González Vega', '15.678.901-3', 'PARENT', '1950-04-20', '+56967890123', NULL, true, NOW(), NOW()),
    (3, 'Carmen Vega Morales', '16.789.012-4', 'PARENT', '1952-12-05', '+56978901234', NULL, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================
-- 2. MASCOTAS (pets)
-- ============================================

-- Unit 1: Familia Pérez-García
INSERT INTO pets (unit_id, name, species, breed, color, size, registration_number, is_dangerous, notes, created_at, updated_at)
VALUES 
    (1, 'Max', 'DOG', 'Labrador Retriever', 'Dorado', 'LARGE', 'REG-DOG-001', false, 'Muy amigable con niños. Vacunas al día.', NOW(), NOW()),
    (1, 'Luna', 'CAT', 'Persa', 'Blanco', 'MEDIUM', 'REG-CAT-001', false, 'Indoor cat. Alérgica a ciertos alimentos.', NOW(), NOW()),
    (1, 'Rocky', 'DOG', 'Beagle', 'Tricolor', 'MEDIUM', 'REG-DOG-002', false, 'Le gusta mucho correr. Muy juguetón.', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Unit 2: Familia Martínez-Silva
INSERT INTO pets (unit_id, name, species, breed, color, size, registration_number, is_dangerous, notes, created_at, updated_at)
VALUES 
    (2, 'Toby', 'DOG', 'Golden Retriever', 'Dorado claro', 'LARGE', 'REG-DOG-003', false, 'Entrenado. Excelente con visitantes.', NOW(), NOW()),
    (2, 'Michi', 'CAT', 'Común', 'Gris atigrado', 'SMALL', 'REG-CAT-002', false, 'Gato rescatado. Muy independiente.', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Unit 3: Familia González
INSERT INTO pets (unit_id, name, species, breed, color, size, registration_number, is_dangerous, notes, created_at, updated_at)
VALUES 
    (3, 'Coco', 'BIRD', 'Cacatúa', 'Blanco', 'SMALL', 'REG-BIRD-001', false, 'Habla algunas palabras. Requiere jaula grande.', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ============================================
-- 3. VEHÍCULOS (vehicles)
-- ============================================

-- Unit 1: Familia Pérez-García
INSERT INTO vehicles (unit_id, license_plate, brand, model, color, vehicle_type, parking_spot, is_active, created_at, updated_at)
VALUES 
    (1, 'BBCD-12', 'Toyota', 'Corolla 2020', 'Blanco', 'CAR', 'A-101', true, NOW(), NOW()),
    (1, 'CDEF-34', 'Hyundai', 'Tucson 2022', 'Gris Oscuro', 'CAR', 'A-102', true, NOW(), NOW()),
    (1, 'FGHJ-56', 'Yamaha', 'MT-07', 'Negro', 'MOTORCYCLE', 'MOTO-01', true, NOW(), NOW())
ON CONFLICT (license_plate) DO NOTHING;

-- Unit 2: Familia Martínez-Silva
INSERT INTO vehicles (unit_id, license_plate, brand, model, color, vehicle_type, parking_spot, is_active, created_at, updated_at)
VALUES 
    (2, 'KLMN-78', 'Mazda', 'CX-5 2021', 'Rojo', 'CAR', 'B-201', true, NOW(), NOW()),
    (2, 'PQRS-90', 'Chevrolet', 'Spark 2019', 'Azul', 'CAR', 'B-202', true, NOW(), NOW())
ON CONFLICT (license_plate) DO NOTHING;

-- Unit 3: Familia González
INSERT INTO vehicles (unit_id, license_plate, brand, model, color, vehicle_type, parking_spot, is_active, created_at, updated_at)
VALUES 
    (3, 'TUVW-12', 'Nissan', 'Sentra 2018', 'Plateado', 'CAR', 'C-301', true, NOW(), NOW()),
    (3, 'BIKE-001', 'Trek', 'Mountain Bike', 'Negro con Rojo', 'BICYCLE', 'BICI-01', true, NOW(), NOW())
ON CONFLICT (license_plate) DO NOTHING;

-- ============================================
-- VERIFICACIÓN
-- ============================================

-- Contar registros insertados
SELECT 'FAMILY MEMBERS' as tabla, COUNT(*) as total FROM family_members
UNION ALL
SELECT 'PETS' as tabla, COUNT(*) as total FROM pets
UNION ALL
SELECT 'VEHICLES' as tabla, COUNT(*) as total FROM vehicles;

-- Mostrar datos por unidad
SELECT 
    u.unit_number,
    COUNT(DISTINCT fm.id) as familia,
    COUNT(DISTINCT p.id) as mascotas,
    COUNT(DISTINCT v.id) as vehiculos
FROM units u
LEFT JOIN family_members fm ON u.id = fm.unit_id
LEFT JOIN pets p ON u.id = p.unit_id
LEFT JOIN vehicles v ON u.id = v.unit_id
WHERE u.id IN (1, 2, 3)
GROUP BY u.id, u.unit_number
ORDER BY u.unit_number;

-- ============================================
-- FIN DEL SCRIPT
-- ============================================
