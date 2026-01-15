-- Insert sample common areas
INSERT INTO common_areas (name, description, capacity, hourly_rate, is_active, building_id, created_at) VALUES
('Quincho', 'Área de parrilla con mesas y sillas para eventos', 30, 15000.0, true, 1, NOW()),
('Sala de Eventos', 'Salón multiuso con proyector y sistema de audio', 50, 25000.0, true, 1, NOW()),
('Piscina', 'Piscina temperada con zona de descanso', 20, 10000.0, true, 1, NOW()),
('Gimnasio', 'Sala de ejercicios con máquinas y pesas', 15, 8000.0, true, 1, NOW()),
('Cancha de Tenis', 'Cancha de tenis con iluminación nocturna', 4, 12000.0, true, 2, NOW()),
('Sala de Juegos', 'Sala recreativa con mesa de pool y juegos de mesa', 12, 5000.0, true, 2, NOW()),
('Terraza BBQ', 'Terraza en el último piso con parrilla', 25, 18000.0, true, 3, NOW()),
('Sala de Cine', 'Mini cine con pantalla grande y butacas', 20, 20000.0, true, 3, NOW());
