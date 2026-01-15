-- Script SQL para crear las nuevas tablas del sistema

-- Tabla de miembros de familia
CREATE TABLE IF NOT EXISTS family_members (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    rut VARCHAR(20),
    relationship VARCHAR(50),
    birth_date DATE,
    phone VARCHAR(50),
    email VARCHAR(255),
    emergency_contact BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (unit_id) REFERENCES units(id) ON DELETE CASCADE
);

-- Tabla de mascotas
CREATE TABLE IF NOT EXISTS pets (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    species VARCHAR(50),
    breed VARCHAR(100),
    color VARCHAR(50),
    size VARCHAR(20),
    registration_number VARCHAR(100),
    is_dangerous BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (unit_id) REFERENCES units(id) ON DELETE CASCADE
);

-- Tabla de vehículos
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    brand VARCHAR(100),
    model VARCHAR(100),
    color VARCHAR(50),
    vehicle_type VARCHAR(50),
    parking_spot VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (unit_id) REFERENCES units(id) ON DELETE CASCADE
);

-- Tabla de lista blanca
CREATE TABLE IF NOT EXISTS whitelist_contacts (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    rut VARCHAR(20) NOT NULL,
    relationship VARCHAR(100),
    phone VARCHAR(50),
    has_perma_access BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (unit_id) REFERENCES units(id) ON DELETE CASCADE
);

-- Índices para mejorar performance
CREATE INDEX idx_family_members_unit ON family_members(unit_id);
CREATE INDEX idx_pets_unit ON pets(unit_id);
CREATE INDEX idx_vehicles_unit ON vehicles(unit_id);
CREATE INDEX idx_vehicles_plate ON vehicles(license_plate);
CREATE INDEX idx_whitelist_unit ON whitelist_contacts(unit_id);

-- Las colecciones de MongoDB (documents y broadcasts) se crean automáticamente
-- al insertar el primer documento en cada una.
