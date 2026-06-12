-- ============================================================
-- DATA.SQL  —  Farmacia Multisede · Datos iniciales de prueba
--
-- *** CREDENCIALES DE ACCESO (todos usan password: "password") ***
--
--   ADMINISTRADOR
--     Email   : admin@farmacia.pe
--     Password: password
--
--   FARMACÉUTICOS DE PRUEBA
--     farmac1@farmacia.pe  →  Sede Central
--     farmac2@farmacia.pe  →  Sede Miraflores
--     farmac3@farmacia.pe  →  Sede San Isidro
--
--   GESTOR
--     gestor@farmacia.pe   →  Sede Central
--
-- *** IMPORTANTE: el admin crea nuevos usuarios desde /admin ***
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- ROLES
-- ------------------------------------------------------------
INSERT IGNORE INTO rol (id_rol, nombre) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_GESTOR'),
(3, 'ROLE_FARMACEUTICO'),
(4, 'ROLE_TRANSPORTISTA');

-- ------------------------------------------------------------
-- SEDES
-- ------------------------------------------------------------
-- ------------------------------------------------------------
-- SEDES
-- ------------------------------------------------------------
INSERT IGNORE INTO sede (id_sede, nombre, direccion) VALUES
(1, 'Sede Central',    'Av. Abancay 491, Lima'),
(2, 'Sede Miraflores', 'Av. Larco 123, Miraflores'),
(3, 'Sede San Isidro', 'Calle Los Rosales 456, San Isidro');

-- ------------------------------------------------------------
-- MEDICAMENTOS
-- ------------------------------------------------------------
INSERT IGNORE INTO medicamento (id_medicamento, nombre, descripcion) VALUES
(1,  'Paracetamol 500mg',              'Analgésico y antipirético de uso común'),
(2,  'Ibuprofeno 400mg',               'Antiinflamatorio no esteroideo'),
(3,  'Amoxicilina 500mg',              'Antibiótico de amplio espectro'),
(4,  'Omeprazol 20mg',                 'Inhibidor de la bomba de protones'),
(5,  'Losartán 50mg',                  'Antihipertensivo antagonista de angiotensina II'),
(6,  'Metformina 850mg',               'Hipoglucemiante oral para diabetes tipo 2'),
(7,  'Atorvastatina 20mg',             'Hipolipemiante para control del colesterol'),
(8,  'Diclofenaco 75mg',               'Antiinflamatorio y analgésico inyectable'),
(9,  'Azitromicina 500mg',             'Antibiótico macrólido de amplio espectro'),
(10, 'Clonazepam 2mg',                 'Anticonvulsivante y ansiolítico'),
(11, 'Salbutamol inhalador 200 dosis', 'Broncodilatador para asma y EPOC'),
(12, 'Insulina NPH 100UI/mL',          'Insulina de acción intermedia para diabetes');

-- ------------------------------------------------------------
-- USUARIOS  (contraseña: "password")
-- ------------------------------------------------------------
INSERT IGNORE INTO usuario (id_usuario, username, nombre, apellido, email, password, telefono, dni, activo, fecha_creacion, id_rol, id_sede) VALUES
(1, 'admin',         'Carlos', 'Mendoza', 'admin@farmacia.pe',         '$2a$12$qxt0EGSyeEkEeJHLKJ04u.S/RBy.QxPQBJlTI2ShN9uiw6iU0Gy2u', '987654321', '12345678', 1, '2024-01-01 08:00:00', 1, 1),
(2, 'gestor',        'María',  'Torres',  'gestor@farmacia.pe',        '$2a$12$qxt0EGSyeEkEeJHLKJ04u.S/RBy.QxPQBJlTI2ShN9uiw6iU0Gy2u', '987654322', '23456789', 1, '2024-01-01 08:00:00', 2, 1),
(3, 'transportista', 'Luis',   'García',  'transportista@farmacia.pe', '$2a$12$qxt0EGSyeEkEeJHLKJ04u.S/RBy.QxPQBJlTI2ShN9uiw6iU0Gy2u', '987654323', '34567890', 1, '2024-01-01 08:00:00', 4, 1),
(4, 'farmac1',       'Ana',    'Quispe',  'farmac1@farmacia.pe',       '$2a$12$qxt0EGSyeEkEeJHLKJ04u.S/RBy.QxPQBJlTI2ShN9uiw6iU0Gy2u', '987654324', '45678901', 1, '2024-01-01 08:00:00', 3, 1),
(5, 'farmac2',       'Pedro',  'Vargas',  'farmac2@farmacia.pe',       '$2a$12$qxt0EGSyeEkEeJHLKJ04u.S/RBy.QxPQBJlTI2ShN9uiw6iU0Gy2u', '987654325', '56789012', 1, '2024-01-01 08:00:00', 3, 2),
(6, 'farmac3',       'Rosa',   'Mamani',  'farmac3@farmacia.pe',       '$2a$12$qxt0EGSyeEkEeJHLKJ04u.S/RBy.QxPQBJlTI2ShN9uiw6iU0Gy2u', '987654326', '67890123', 1, '2024-01-01 08:00:00', 3, 3),
(7, 'gestor2',       'Jorge',  'Ramos',   'gestor2@farmacia.pe',       '$2a$12$qxt0EGSyeEkEeJHLKJ04u.S/RBy.QxPQBJlTI2ShN9uiw6iU0Gy2u', '987654327', '78901234', 1, '2024-01-01 08:00:00', 2, 2);

-- ------------------------------------------------------------
-- MEDICAMENTO_SEDE  (stock por (medicamento, sede))
-- Sede 1 Central   → stock alto
-- Sede 2 Miraflores → stock medio
-- Sede 3 San Isidro → varios con stock bajo/crítico
-- ------------------------------------------------------------
INSERT IGNORE INTO medicamento_sede (id, id_medicamento, id_sede, stock_total, precio) VALUES
-- Sede 1
(1,  1,  1, 150, 5.50), (2,  2,  1, 140, 8.00), (3,  3,  1,  75, 12.50), (4,  4,  1, 200, 15.00),
(5,  5,  1, 180, 20.00), (6,  6,  1, 160, 18.00), (7,  7,  1, 100, 25.00), (8,  8,  1,  80, 10.00),
(9,  9,  1,  90, 22.00), (10, 10, 1,  45, 30.00), (11, 11, 1,  40, 35.00), (12, 12, 1,  60, 45.00),
-- Sede 2
(13, 1,  2, 100, 5.50), (14, 2,  2,  90, 8.00), (15, 3,  2,  70, 12.50), (16, 4,  2, 150, 15.00),
(17, 5,  2, 120, 20.00), (18, 6,  2, 100, 18.00), (19, 7,  2,  70, 25.00), (20, 8,  2,  60, 10.00),
(21, 9,  2,  50, 22.00), (22, 10, 2,  30, 30.00), (23, 11, 2,  20, 35.00), (24, 12, 2,  40, 45.00),
-- Sede 3  (★ = bajo stock para disparar notificaciones)
(25, 1,  3,  30, 5.50), (26, 2,  3,  12, 8.00), (27, 3,  3,   8, 12.50), (28, 4,  3,  50, 15.00),
(29, 5,  3,  25, 20.00), (30, 6,  3,  20, 18.00), (31, 7,  3,   4, 25.00), (32, 8,  3,  40, 10.00),
(33, 9,  3,   9, 22.00), (34, 10, 3,   3, 30.00), (35, 11, 3,   7, 35.00), (36, 12, 3,  15, 45.00);

-- ------------------------------------------------------------
-- LOTES
-- Referencia de fechas (hoy: 2026-05-31):
--   Caducados        : antes de 2026-05-31
--   Próximos caducar : 2026-06-01 al 2026-06-30
--   Vigentes         : 2027 en adelante
-- ------------------------------------------------------------

-- Sede 1 (Central)
INSERT IGNORE INTO lote (id_lote, codigo_lote, fecha_caducidad, stock_lote, id_medicamento, id_sede) VALUES
(1,  'LOT-202801-011A', '2028-01-01', 100, 1,  1),
(2,  'LOT-202606-011B', '2026-06-20',  50, 1,  1),  -- próximo a caducar
(3,  'LOT-202803-021A', '2028-03-01',  80, 2,  1),
(4,  'LOT-202806-021B', '2028-06-01',  60, 2,  1),
(5,  'LOT-202706-031A', '2027-06-01',  70, 3,  1),
(6,  'LOT-202604-031B', '2026-04-15',   5, 3,  1),  -- caducado
(7,  'LOT-202901-041A', '2029-01-01', 200, 4,  1),
(8,  'LOT-202801-051A', '2028-01-01', 100, 5,  1),
(9,  'LOT-202706-051B', '2027-06-01',  80, 5,  1),
(10, 'LOT-202712-061A', '2027-12-01', 160, 6,  1),
(11, 'LOT-202703-071A', '2027-03-01', 100, 7,  1),
(12, 'LOT-202801-081A', '2028-01-01',  80, 8,  1),
(13, 'LOT-202709-091A', '2027-09-01',  90, 9,  1),
(14, 'LOT-202606-101A', '2026-06-15',  45, 10, 1),  -- próximo a caducar
(15, 'LOT-202701-111A', '2027-01-01',  40, 11, 1),
(16, 'LOT-202606-121A', '2026-06-28',  60, 12, 1);  -- próximo a caducar

-- Sede 2 (Miraflores)
INSERT IGNORE INTO lote (id_lote, codigo_lote, fecha_caducidad, stock_lote, id_medicamento, id_sede) VALUES
(17, 'LOT-202801-012A', '2028-01-01', 100, 1,  2),
(18, 'LOT-202706-022A', '2027-06-01',  90, 2,  2),
(19, 'LOT-202706-032A', '2027-06-01',  70, 3,  2),
(20, 'LOT-202901-042A', '2029-01-01', 150, 4,  2),
(21, 'LOT-202801-052A', '2028-01-01', 120, 5,  2),
(22, 'LOT-202712-062A', '2027-12-01', 100, 6,  2),
(23, 'LOT-202703-072A', '2027-03-01',  70, 7,  2),
(24, 'LOT-202801-082A', '2028-01-01',  60, 8,  2),
(25, 'LOT-202709-092A', '2027-09-01',  50, 9,  2),
(26, 'LOT-202701-102A', '2027-01-01',  30, 10, 2),
(27, 'LOT-202606-112A', '2026-06-25',  20, 11, 2),  -- próximo a caducar
(28, 'LOT-202606-122A', '2026-06-28',  40, 12, 2);  -- próximo a caducar

-- Sede 3 (San Isidro)
INSERT IGNORE INTO lote (id_lote, codigo_lote, fecha_caducidad, stock_lote, id_medicamento, id_sede) VALUES
(29, 'LOT-202706-013A', '2027-06-01',  30, 1,  3),
(30, 'LOT-202703-023A', '2027-03-01',  12, 2,  3),
(31, 'LOT-202701-033A', '2027-01-01',   8, 3,  3),
(32, 'LOT-202801-043A', '2028-01-01',  50, 4,  3),
(33, 'LOT-202706-053A', '2027-06-01',  25, 5,  3),
(34, 'LOT-202712-063A', '2027-12-01',  20, 6,  3),
(35, 'LOT-202703-073A', '2027-03-01',   4, 7,  3),
(36, 'LOT-202801-083A', '2028-01-01',  40, 8,  3),
(37, 'LOT-202709-093A', '2027-09-01',   9, 9,  3),
(38, 'LOT-202606-103A', '2026-06-12',   3, 10, 3),  -- próximo a caducar
(39, 'LOT-202701-113A', '2027-01-01',   7, 11, 3),
(40, 'LOT-202606-123A', '2026-06-25',  15, 12, 3);  -- próximo a caducar

-- ------------------------------------------------------------
-- MOVIMIENTO_STOCK  (historial representativo)
-- ------------------------------------------------------------
INSERT IGNORE INTO movimiento_stock (id_movimiento, tipo, cantidad, fecha, observacion, id_medicamento, id_sede, id_lote, id_usuario) VALUES
(1,  'ENTRADA',       100, '2024-01-15 09:00:00', 'Ingreso inicial de lote',         1,  1, 1,  4),
(2,  'ENTRADA',        50, '2024-01-15 09:05:00', 'Ingreso inicial de lote',         1,  1, 2,  4),
(3,  'ENTRADA',       200, '2024-01-15 09:10:00', 'Ingreso inicial de lote',         4,  1, 7,  4),
(4,  'ENTRADA',       100, '2024-01-15 09:15:00', 'Ingreso inicial de lote',         5,  1, 8,  4),
(5,  'SALIDA',         30, '2026-05-10 10:00:00', 'Despacho por receta médica',      1,  1, 1,  4),
(6,  'SALIDA',         20, '2026-05-15 11:00:00', 'Despacho por receta médica',      2,  1, 3,  4),
(7,  'VENCIMIENTO',     5, '2026-05-20 08:00:00', 'Retiro por caducidad',            3,  1, 6,  4),
(8,  'TRANSFERENCIA',  30, '2026-05-20 14:00:00', 'Transferencia a Sede San Isidro', 3,  1, 5,  2),
(9,  'ENTRADA',        30, '2026-05-20 16:00:00', 'Recepción de transferencia',      3,  3, NULL, 6),
(10, 'SALIDA',         10, '2026-05-28 09:00:00', 'Despacho por receta médica',      6,  2, 22, 5);

-- ------------------------------------------------------------
-- ORDENES
-- ------------------------------------------------------------
INSERT IGNORE INTO orden (id_orden, tipo, estado, fecha, id_usuario, id_sede, id_sede_destino) VALUES
(1, 'TRANSFERENCIA', 'COMPLETADA', '2026-05-10 14:00:00', 2, 1, 3),
(2, 'COMPRA',        'PENDIENTE',  '2026-05-25 10:30:00', 2, 1, NULL),
(3, 'TRANSFERENCIA', 'APROBADA',   '2026-05-28 09:00:00', 7, 2, 3);

-- ------------------------------------------------------------
-- DETALLE_ORDEN
-- ------------------------------------------------------------
INSERT IGNORE INTO detalle_orden (id_detalle, cantidad, estado, id_orden, id_medicamento) VALUES
(1, 50, 'ENTREGADO',      1, 3),   -- Amoxicilina transferida (Sede 1 → 3), completada
(2, 50, 'PENDIENTE',      2, 7),   -- Atorvastatina en compra pendiente (Sede 1)
(3, 30, 'PENDIENTE',      2, 10),  -- Clonazepam en compra pendiente (Sede 1)
(4, 20, 'EN_RUTA',        3, 7),   -- Atorvastatina en tránsito (Sede 2 → 3)
(5, 15, 'EN_PREPARACION', 3, 9);   -- Azitromicina en preparación (Sede 2 → 3)

-- ------------------------------------------------------------
-- NOTIFICACIONES
-- ------------------------------------------------------------
INSERT IGNORE INTO notificacion (id_notificacion, mensaje, fecha, estado, tipo, id_medicamento, id_sede) VALUES
-- Bajo stock en Sede 3
(1,  'Stock crítico: Ibuprofeno 400mg en Sede San Isidro (12 unidades)',            '2026-05-30 08:00:00', 'PENDIENTE', 'BAJO_STOCK',      2,  3),
(2,  'Stock crítico: Amoxicilina 500mg en Sede San Isidro (8 unidades)',             '2026-05-30 08:01:00', 'PENDIENTE', 'BAJO_STOCK',      3,  3),
(3,  'Stock crítico: Atorvastatina 20mg en Sede San Isidro (4 unidades)',            '2026-05-30 08:02:00', 'PENDIENTE', 'BAJO_STOCK',      7,  3),
(4,  'Stock crítico: Azitromicina 500mg en Sede San Isidro (9 unidades)',            '2026-05-30 08:03:00', 'PENDIENTE', 'BAJO_STOCK',      9,  3),
(5,  'Stock crítico: Clonazepam 2mg en Sede San Isidro (3 unidades)',                '2026-05-30 08:04:00', 'PENDIENTE', 'BAJO_STOCK',      10, 3),
(6,  'Stock crítico: Salbutamol inhalador en Sede San Isidro (7 unidades)',          '2026-05-30 08:05:00', 'PENDIENTE', 'BAJO_STOCK',      11, 3),
-- Próximos a caducar - Sede 1
(7,  'Próximo a caducar: Paracetamol 500mg, lote LOT-202606-011B (20/06/2026, 50u)','2026-05-31 07:00:00', 'PENDIENTE', 'PROXIMO_CADUCAR', 1,  1),
(8,  'Próximo a caducar: Clonazepam 2mg, lote LOT-202606-101A (15/06/2026, 45u)',   '2026-05-31 07:01:00', 'PENDIENTE', 'PROXIMO_CADUCAR', 10, 1),
(9,  'Próximo a caducar: Insulina NPH, lote LOT-202606-121A (28/06/2026, 60u)',     '2026-05-31 07:02:00', 'PENDIENTE', 'PROXIMO_CADUCAR', 12, 1),
-- Próximos a caducar - Sede 2
(10, 'Próximo a caducar: Salbutamol inhalador, lote LOT-202606-112A (25/06/2026, 20u)', '2026-05-31 07:03:00', 'PENDIENTE', 'PROXIMO_CADUCAR', 11, 2),
(11, 'Próximo a caducar: Insulina NPH, lote LOT-202606-122A (28/06/2026, 40u)',     '2026-05-31 07:04:00', 'PENDIENTE', 'PROXIMO_CADUCAR', 12, 2),
-- Próximos a caducar - Sede 3
(12, 'Próximo a caducar: Clonazepam 2mg, lote LOT-202606-103A (12/06/2026, 3u)',    '2026-05-31 07:05:00', 'PENDIENTE', 'PROXIMO_CADUCAR', 10, 3),
(13, 'Próximo a caducar: Insulina NPH, lote LOT-202606-123A (25/06/2026, 15u)',     '2026-05-31 07:06:00', 'PENDIENTE', 'PROXIMO_CADUCAR', 12, 3),
-- Ya atendida (orden generada desde esta notificación)
(14, 'Stock crítico: Amoxicilina 500mg en Sede San Isidro — orden de transferencia generada', '2026-05-09 10:00:00', 'ATENDIDA', 'BAJO_STOCK', 3, 3);

-- ------------------------------------------------------------
-- VENTAS (ejemplo)
-- ------------------------------------------------------------
INSERT IGNORE INTO venta (id_venta, id_usuario, id_sede, fecha, total) VALUES
(1, 4, 1, '2026-05-28 10:30:00', 27.50),
(2, 4, 1, '2026-05-29 15:45:00', 95.00),
(3, 5, 2, '2026-05-30 09:15:00', 42.00);

INSERT IGNORE INTO detalle_venta (id_detalle, id_venta, id_medicamento, cantidad, precio_unitario, subtotal) VALUES
-- Venta 1 (farmac1 - Sede Central): 3 Paracetamol + 2 Ibuprofeno
(1, 1, 1, 3, 5.50, 16.50),
(2, 1, 2, 2, 8.00, 16.00),
-- Venta 2 (farmac1 - Sede Central): 2 Omeprazol + 1 Losartán + 1 Atorvastatina
(3, 2, 4, 2, 15.00, 30.00),
(4, 2, 5, 1, 20.00, 20.00),
(5, 2, 7, 1, 25.00, 25.00),
-- Venta 3 (farmac2 - Sede Miraflores): 4 Paracetamol + 2 Ibuprofeno
(6, 3, 1, 4, 5.50, 22.00),
(7, 3, 2, 2, 8.00, 16.00);

SET FOREIGN_KEY_CHECKS = 1;
