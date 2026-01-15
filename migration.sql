-- Migración para agregar columnas nuevas a common_areas
ALTER TABLE common_areas ADD COLUMN IF NOT EXISTS requires_approval BOOLEAN DEFAULT true;
ALTER TABLE common_areas ADD COLUMN IF NOT EXISTS time_blocks TEXT;
ALTER TABLE common_areas ADD COLUMN IF NOT EXISTS max_advance_days INTEGER DEFAULT 30;

-- Actualizar registros existentes
UPDATE common_areas SET requires_approval = true WHERE requires_approval IS NULL;
UPDATE common_areas SET max_advance_days = 30 WHERE max_advance_days IS NULL;

-- Agregar restricción NOT NULL después de actualizar
ALTER TABLE common_areas ALTER COLUMN requires_approval SET NOT NULL;

-- Agregar columnas nuevas a reservations
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS payment_proof_url VARCHAR(500);
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS check_in_time TIMESTAMP;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS check_out_time TIMESTAMP;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS approved_by BIGINT;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS requires_cleaning BOOLEAN DEFAULT false;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS cleaning_notes TEXT;

-- Crear tabla reservation_guests si no existe
CREATE TABLE IF NOT EXISTS reservation_guests (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    rut VARCHAR(20) NOT NULL,
    phone VARCHAR(20),
    checked_in BOOLEAN DEFAULT false,
    check_in_time TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);
