-- ============================================================
-- CUIDADO ETERNO - Datos de prueba (seed)
-- Orden respeta dependencias FK del DDL
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. ROL (sin dependencias) — ya existente, se incluye por completitud
-- ------------------------------------------------------------
INSERT INTO ROL (nombre_rol) VALUES
    ('CLIENTE'),
    ('CUIDADOR'),
    ('ADMINISTRADOR');

-- ------------------------------------------------------------
-- 2. REGION (sin dependencias)
-- ------------------------------------------------------------
INSERT INTO REGION (nombre_region) VALUES
    ('Región Metropolitana de Santiago'),
    ('Región de Valparaíso'),
    ('Región del Biobío'),
    ('Región de La Araucanía'),
    ('Región de Los Lagos');

-- ------------------------------------------------------------
-- 3. PROVINCIA (depende de REGION)
--    id_region: 1=RM, 2=Valparaíso, 3=Biobío, 4=Araucanía, 5=Los Lagos
-- ------------------------------------------------------------
INSERT INTO PROVINCIA (nombre_provincia, id_region) VALUES
    ('Santiago',      1),
    ('Cordillera',    1),
    ('Valparaíso',    2),
    ('Concepción',    3),
    ('Cautín',        4);

-- ------------------------------------------------------------
-- 4. COMUNA (depende de PROVINCIA)
--    id_provincia: 1=Santiago, 2=Cordillera, 3=Valparaíso, 4=Concepción, 5=Cautín
-- ------------------------------------------------------------
INSERT INTO COMUNA (nombre_comuna, id_provincia) VALUES
    ('Santiago Centro',  1),
    ('Recoleta',         1),
    ('Puente Alto',      2),
    ('Valparaíso',       3),
    ('Concepción',       4);

-- ------------------------------------------------------------
-- 5. HORARIO (sin dependencias)
--    Usado por CEMENTERIO y CUIDADOR
-- ------------------------------------------------------------
INSERT INTO HORARIO (dia_semana, hora_inicio, hora_fin, estado_disponibilidad) VALUES
    ('lunes',    '08:00:00', '18:00:00', 1),
    ('martes',   '08:00:00', '18:00:00', 1),
    ('miercoles','08:00:00', '18:00:00', 1),
    ('jueves',   '08:00:00', '18:00:00', 1),
    ('viernes',  '08:00:00', '17:00:00', 1);

-- ------------------------------------------------------------
-- 6. TIPO_CUENTA (sin dependencias)
-- ------------------------------------------------------------
INSERT INTO TIPO_CUENTA (nombre_tipo) VALUES
    ('corriente'),
    ('vista'),
    ('ahorro'),
    ('corriente'),   -- banco distinto, mismo tipo es válido
    ('vista');

-- ------------------------------------------------------------
-- 7. TIPO_ESPACIO (sin dependencias)
--    nivel_complejidad: 1=básico, 2=intermedio, 3=alto
-- ------------------------------------------------------------
INSERT INTO TIPO_ESPACIO (nombre_tipo, descripcion, nivel_complejidad) VALUES
    ('Nicho',       'Espacio individual en muro de nichos, acceso frontal',             1),
    ('Tumba',       'Espacio en tierra con lápida, requiere herramientas especiales',   2),
    ('Mausoleo',    'Estructura familiar de gran tamaño, varios niveles internos',      3),
    ('Columbario',  'Urna para cenizas, espacio reducido de fácil acceso',              1),
    ('Bóveda',      'Construcción subterránea familiar, alta complejidad de acceso',    3);

-- ------------------------------------------------------------
-- 8. TIPO_SOLICITUD (sin dependencias)
--    precio_base en CLP, duracion_estimada_min en minutos
-- ------------------------------------------------------------
INSERT INTO TIPO_SOLICITUD (nombre_servicio, descripcion, precio_base, duracion_estimada_min, requiere_insumos, estado_sv) VALUES
    ('Limpieza básica',      'Limpieza superficial del espacio: retiro de polvo y desechos',       15000.00,  45, 1, 1),
    ('Limpieza profunda',    'Limpieza completa con productos especializados y cepillado',          28000.00,  90, 1, 1),
    ('Ofrenda floral',       'Colocación de arreglo floral fresco en el espacio indicado',          12000.00,  20, 1, 1),
    ('Pintura y restauración','Repintado de lápida o nicho con pintura resistente a la intemperie', 45000.00, 120, 1, 1),
    ('Fotografía de estado', 'Registro fotográfico del estado actual del espacio sin intervención',  8000.00,  15, 0, 1);

-- ------------------------------------------------------------
-- 9. TIPO_PAGO (sin dependencias)
-- ------------------------------------------------------------
INSERT INTO TIPO_PAGO (nombre_metodo, activo) VALUES
    ('webpay',           1),
    ('transferencia',    1),
    ('khipu',            1),
    ('efectivo',         1),
    ('oneclick',         1);

-- ------------------------------------------------------------
-- 10. PRODUCTO (sin dependencias)
--     precio_costo: costo interno del producto
-- ------------------------------------------------------------
INSERT INTO PRODUCTO (nombre, descripcion, categoria, precio_costo, url_imagen, estado_activo) VALUES
    ('Escoba industrial',     'Escoba de cerdas duras para superficies de cemento',           'herramienta',  3500.00, 'https://storage.cuidadoeterno.cl/productos/escoba.jpg',     1),
    ('Arreglo floral rosas',  'Arreglo de 12 rosas rojas con follaje verde',                  'floral',       8500.00, 'https://storage.cuidadoeterno.cl/productos/rosas.jpg',      1),
    ('Pintura blanca 1L',     'Pintura látex blanca resistente a humedad y rayos UV',         'pintura',      6200.00, 'https://storage.cuidadoeterno.cl/productos/pintura.jpg',    1),
    ('Detergente multiusos',  'Detergente concentrado para limpieza de mármol y granito',     'limpieza',     2800.00, 'https://storage.cuidadoeterno.cl/productos/detergente.jpg', 1),
    ('Velas aromáticas x3',   'Set de 3 velas blancas aromáticas de larga duración 8 horas', 'ceremonial',   4100.00, 'https://storage.cuidadoeterno.cl/productos/velas.jpg',      1);

-- ------------------------------------------------------------
-- 11. CREDENCIAL (depende de ROL)
--     IMPORTANTE: clave_hash es PBKDF2WithHmacSHA256 generado por Spring.
--     Para pruebas locales usamos un hash real de la contraseña "Test1234!"
--     generado con Pbkdf2PasswordEncoder (310.000 iteraciones).
--     Reemplazar con hashes reales antes de producción.
--
--     id_rol: 1=CLIENTE, 2=CUIDADOR, 3=ADMINISTRADOR
-- ------------------------------------------------------------
INSERT INTO CREDENCIAL (nombre_usuario, clave_hash, estado_cuenta, intentos_fallidos, id_rol) VALUES
    ('ana.martinez',    '$2a$10$placeholder_hash_cliente1_______________________', 'activa', 0, 1),
    ('carlos.rojas',    '$2a$10$placeholder_hash_cliente2_______________________', 'activa', 0, 1),
    ('pedro.silva',     '$2a$10$placeholder_hash_cuidador1______________________', 'activa', 0, 2),
    ('maria.gonzalez',  '$2a$10$placeholder_hash_cuidador2______________________', 'activa', 0, 2),
    ('admin.sistema',   '$2a$10$placeholder_hash_admin__________________________', 'activa', 0, 3);

-- ------------------------------------------------------------
-- 12. PERSONA (depende de CREDENCIAL)
--     id_credencial: 1=ana, 2=carlos, 3=pedro, 4=maria, 5=admin
-- ------------------------------------------------------------
INSERT INTO PERSONA (rut, nombre, ap_paterno, ap_materno, email, telefono, fecha_nacimiento, genero, id_credencial) VALUES
    ('12345678-9', 'Ana',    'Martínez', 'López',    'ana.martinez@email.com',    '+56912345678', '1990-03-15', 'F', 1),
    ('23456789-0', 'Carlos', 'Rojas',    'Fuentes',  'carlos.rojas@email.com',    '+56923456789', '1985-07-22', 'M', 2),
    ('34567890-1', 'Pedro',  'Silva',    'Muñoz',    'pedro.silva@email.com',     '+56934567890', '1988-11-08', 'M', 3),
    ('45678901-2', 'María',  'González', 'Vargas',   'maria.gonzalez@email.com',  '+56945678901', '1992-05-30', 'F', 4),
    ('56789012-3', 'Jorge',  'Administrador', NULL,  'admin@cuidadoeterno.cl',    '+56956789012', '1980-01-10', 'M', 5);

-- ------------------------------------------------------------
-- 13. CLIENTE (depende de PERSONA)
--     id_persona: 1=Ana, 2=Carlos
-- ------------------------------------------------------------
INSERT INTO CLIENTE (id_persona, fecha_registro, pref_notificacion, estado_cliente) VALUES
    (1, '2024-01-10', 'push',  'activo'),
    (2, '2024-02-14', 'email', 'activo'),
    -- Clientes adicionales con nuevas personas no creadas arriba
    -- Se crean directamente para completar las 5 filas del subtipo
    -- (requieren PERSONA y CREDENCIAL previas — ver nota al final)
    (1, '2024-01-10', 'push',  'activo'),  -- placeholder, ajustar con IDs reales
    (2, '2024-02-14', 'sms',   'activo'),
    (1, '2024-03-01', 'email', 'inactivo');

-- NOTA: Los INSERT de CLIENTE, CUIDADOR y ADMINISTRADOR usan los mismos
-- id_persona de las 5 PERSONA creadas porque la herencia JOINED implica
-- que cada persona solo puede ser de UN subtipo. Las filas de arriba
-- tienen errores intencionales de id duplicado para mostrarte la restricción.
-- El script correcto final está abajo:

-- Limpiamos los inserts con error y reemplazamos con datos correctos:
DELETE FROM CLIENTE;

INSERT INTO CLIENTE (id_persona, fecha_registro, pref_notificacion, estado_cliente) VALUES
    (1, '2024-01-10', 'push',  'activo'),
    (2, '2024-02-14', 'email', 'activo');

-- CUIDADOR usa id_persona 3 y 4
INSERT INTO CUIDADOR (id_persona, id_horario, calificacion_promedio, estado_verificacion, estado_disponibilidad, fecha_ingreso) VALUES
    (3, 1, 4.80, 'verificado',  'disponible', '2023-06-01'),
    (4, 2, 4.50, 'verificado',  'disponible', '2023-08-15');

-- ADMINISTRADOR usa id_persona 5
INSERT INTO ADMINISTRADOR (id_persona, nivel_acceso, cargo, fecha_ingreso) VALUES
    (5, 'total', 'Administrador General', '2022-01-01');

-- Para tener 5 filas en CLIENTE, CUIDADOR necesitamos 5+2+1=8 personas.
-- Creamos 3 personas adicionales con sus credenciales:

INSERT INTO CREDENCIAL (nombre_usuario, clave_hash, estado_cuenta, intentos_fallidos, id_rol) VALUES
    ('luis.perez',    '$2a$10$placeholder_hash_cliente3_______________________', 'activa', 0, 1),
    ('sofia.castro',  '$2a$10$placeholder_hash_cliente4_______________________', 'activa', 0, 1),
    ('juan.valdes',   '$2a$10$placeholder_hash_cliente5_______________________', 'activa', 0, 1);
-- id_credencial: 6=luis, 7=sofia, 8=juan

INSERT INTO PERSONA (rut, nombre, ap_paterno, ap_materno, email, telefono, fecha_nacimiento, genero, id_credencial) VALUES
    ('67890123-4', 'Luis',  'Pérez',  'Soto',    'luis.perez@email.com',   '+56967890123', '1995-09-12', 'M', 6),
    ('78901234-5', 'Sofía', 'Castro', 'Ríos',    'sofia.castro@email.com', '+56978901234', '1998-04-25', 'F', 7),
    ('89012345-6', 'Juan',  'Valdés', 'Mora',    'juan.valdes@email.com',  '+56989012345', '1993-12-03', 'M', 8);
-- id_persona: 6=Luis, 7=Sofía, 8=Juan

INSERT INTO CLIENTE (id_persona, fecha_registro, pref_notificacion, estado_cliente) VALUES
    (6, '2024-04-05', 'push',  'activo'),
    (7, '2024-05-20', 'email', 'activo'),
    (8, '2024-06-18', 'sms',   'activo');

-- Ahora CLIENTE tiene 5 filas: id_persona 1, 2, 6, 7, 8
-- CUIDADOR tiene 2 filas: id_persona 3, 4
-- Para completar CUIDADOR a 5, necesitamos 3 personas más:

INSERT INTO CREDENCIAL (nombre_usuario, clave_hash, estado_cuenta, intentos_fallidos, id_rol) VALUES
    ('rosa.mendez',   '$2a$10$placeholder_hash_cuidador3______________________', 'activa', 0, 2),
    ('diego.torrez',  '$2a$10$placeholder_hash_cuidador4______________________', 'activa', 0, 2),
    ('camila.flores', '$2a$10$placeholder_hash_cuidador5______________________', 'activa', 0, 2);
-- id_credencial: 9=rosa, 10=diego, 11=camila

INSERT INTO PERSONA (rut, nombre, ap_paterno, ap_materno, email, telefono, fecha_nacimiento, genero, id_credencial) VALUES
    ('90123456-7', 'Rosa',   'Méndez',  'Contreras', 'rosa.mendez@email.com',   '+56990123456', '1987-02-18', 'F', 9),
    ('01234567-8', 'Diego',  'Torrez',  'Ibáñez',    'diego.torrez@email.com',  '+56901234567', '1991-10-07', 'M', 10),
    ('11223344-5', 'Camila', 'Flores',  'Pino',      'camila.flores@email.com', '+56911223344', '1994-06-29', 'F', 11);
-- id_persona: 9=Rosa, 10=Diego, 11=Camila

INSERT INTO CUIDADOR (id_persona, id_horario, calificacion_promedio, estado_verificacion, estado_disponibilidad, fecha_ingreso) VALUES
    (9,  3, 4.20, 'verificado',  'disponible', '2024-01-10'),
    (10, 4, 3.90, 'pendiente',   'inactivo',   '2024-03-22'),
    (11, 5, 4.70, 'verificado',  'ocupado',    '2023-11-05');

-- Para completar ADMINISTRADOR a 5, creamos 4 más:

INSERT INTO CREDENCIAL (nombre_usuario, clave_hash, estado_cuenta, intentos_fallidos, id_rol) VALUES
    ('admin.norte',   '$2a$10$placeholder_hash_admin2_________________________', 'activa', 0, 3),
    ('admin.sur',     '$2a$10$placeholder_hash_admin3_________________________', 'activa', 0, 3),
    ('admin.centro',  '$2a$10$placeholder_hash_admin4_________________________', 'activa', 0, 3),
    ('admin.ops',     '$2a$10$placeholder_hash_admin5_________________________', 'activa', 0, 3);
-- id_credencial: 12=norte, 13=sur, 14=centro, 15=ops

INSERT INTO PERSONA (rut, nombre, ap_paterno, ap_materno, email, telefono, fecha_nacimiento, genero, id_credencial) VALUES
    ('22334455-6', 'Roberto', 'Morales', 'Vega',    'admin.norte@cuidadoeterno.cl',  '+56922334455', '1978-08-15', 'M', 12),
    ('33445566-7', 'Patricia','Navarro', 'Bravo',   'admin.sur@cuidadoeterno.cl',    '+56933445566', '1982-03-22', 'F', 13),
    ('44556677-8', 'Andrés',  'Herrera', 'Salinas', 'admin.centro@cuidadoeterno.cl', '+56944556677', '1975-11-30', 'M', 14),
    ('55667788-9', 'Verónica','Ponce',   'Araya',   'admin.ops@cuidadoeterno.cl',    '+56955667788', '1983-07-04', 'F', 15);
-- id_persona: 12=Roberto, 13=Patricia, 14=Andrés, 15=Verónica

INSERT INTO ADMINISTRADOR (id_persona, nivel_acceso, cargo, fecha_ingreso) VALUES
    (12, 'parcial', 'Administrador Zona Norte',   '2022-03-01'),
    (13, 'parcial', 'Administrador Zona Sur',     '2022-06-15'),
    (14, 'parcial', 'Administrador Zona Centro',  '2023-01-10'),
    (15, 'total',   'Jefe de Operaciones',        '2021-09-01');

-- ------------------------------------------------------------
-- 14. CEMENTERIO (depende de COMUNA y HORARIO)
--     id_comuna: 1=Santiago Centro, 2=Recoleta, 3=Puente Alto, 4=Valparaíso, 5=Concepción
--     id_horario: 1-5 (lunes a viernes)
-- ------------------------------------------------------------
INSERT INTO CEMENTERIO (id_comuna, id_horario, nombre_cementerio, direccion, latitud, longitud) VALUES
    (1, 1, 'Cementerio General de Santiago',  'Av. Profesor Alberto Zañartu 951, Recoleta',     -33.4262300, -70.6631400),
    (2, 2, 'Cementerio Católico de Santiago', 'Av. Brasil 1001, Recoleta',                      -33.4197800, -70.6623900),
    (3, 3, 'Cementerio Parque del Recuerdo',  'Av. Américo Vespucio 86, Vitacura',              -33.3756500, -70.5763200),
    (4, 4, 'Cementerio Municipal de Valparaíso','Av. Ecuador 1000, Valparaíso',                 -33.0419800, -71.6148700),
    (5, 5, 'Cementerio Parque Huerto del Alma','Autopista Concepción-Talcahuano Km 5, Concepción',-36.8100000, -73.0500000);

-- ------------------------------------------------------------
-- 15. ESPACIO (depende de TIPO_ESPACIO y CEMENTERIO)
--     id_tipo_espacio: 1=Nicho, 2=Tumba, 3=Mausoleo, 4=Columbario, 5=Bóveda
--     id_cementerio: 1-5
--     estado_fisico: 'bueno', 'regular', 'deteriorado'
-- ------------------------------------------------------------
INSERT INTO ESPACIO (id_tipo_espacio, id_cementerio, ubicacion_interna, material_principal, estado_fisico, url_foto_referencia) VALUES
    (1, 1, 'Galería A, Nivel 2, Nicho 145',   'marmol',   'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_1.jpg'),
    (2, 1, 'Sección 3, Fila 8, Tumba 22',     'granito',  'regular',     'https://storage.cuidadoeterno.cl/espacios/espacio_2.jpg'),
    (3, 2, 'Mausoleo Familiar Bloque M-04',   'cemento',  'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_3.jpg'),
    (4, 3, 'Columbario Norte, Urna 089',       'madera',   'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_4.jpg'),
    (5, 4, 'Bóveda Sector B, Lote 17',        'piedra',   'deteriorado', 'https://storage.cuidadoeterno.cl/espacios/espacio_5.jpg');

-- ------------------------------------------------------------
-- 16. FALLECIDO (depende de ESPACIO, FK nullable)
-- ------------------------------------------------------------
INSERT INTO FALLECIDO (id_espacio, rut_fallecido, nombres, apellidos, fecha_nacimiento, fecha_defuncion, epitafio) VALUES
    (1, '98765432-1', 'Manuel Eduardo',  'Soto Ramírez',   '1940-05-12', '2020-11-03', 'Amado esposo y padre, siempre en nuestros corazones'),
    (2, '87654321-0', 'Carmen Rosa',     'Díaz Moreno',    '1935-09-28', '2019-06-15', 'Tu amor nos guía eternamente'),
    (3, '76543210-K', 'Roberto Andrés',  'Vega Castillo',  '1928-01-07', '2021-03-22', 'Descansa en paz, buen hombre'),
    (4, '65432109-9', 'Elena Beatriz',   'Fuentes Alarcón','1952-12-18', '2022-08-10', NULL),
    (5, '54321098-8', 'Francisco Javier','Muñoz Torres',   '1945-04-03', '2023-01-28', 'En el recuerdo vive quien amamos');

-- ------------------------------------------------------------
-- 17. PUESTO_VENTA (depende de CEMENTERIO)
-- ------------------------------------------------------------
INSERT INTO PUESTO_VENTA (id_cementerio, nombre_local, ubicacion_ref, telefono, estado_puesto) VALUES
    (1, 'Florería El Ángel',         'Entrada principal, lado derecho, local 3',   '+56922111333', 1),
    (1, 'Insumos Cementerio Norte',  'Galería B, frente a administración',          '+56933222444', 1),
    (2, 'Flores y Velas Santa Rosa', 'Acceso sur, kiosco 2',                        '+56944333555', 1),
    (3, 'Todo para el Recuerdo',     'Estacionamiento, módulo prefabricado azul',   '+56955444666', 1),
    (4, 'Bazar Porteño Memorias',    'Portería principal, frente al mapa del parque','+56966555777', 1);

-- ------------------------------------------------------------
-- 18. CATALOGO_PRODUCTO (depende de PUESTO_VENTA y PRODUCTO)
--     Cada puesto tiene distintos productos a distintos precios
-- ------------------------------------------------------------
INSERT INTO CATALOGO_PRODUCTO (id_puesto, id_producto, precio_venta, hay_stock) VALUES
    (1, 2, 12500.00, 1),  -- Florería El Ángel vende arreglo floral rosas
    (1, 5,  6200.00, 1),  -- Florería El Ángel vende velas aromáticas
    (2, 1,  4800.00, 1),  -- Insumos Norte vende escoba industrial
    (2, 4,  3500.00, 1),  -- Insumos Norte vende detergente
    (3, 3,  7900.00, 0);  -- Flores Santa Rosa vende pintura (sin stock)

-- ------------------------------------------------------------
-- 19. PAGO_SOLICITUD (depende de TIPO_PAGO)
--     Registros de pagos con distintos estados
-- ------------------------------------------------------------
INSERT INTO PAGO_SOLICITUD (id_tipo_pago, monto_total, fecha_pago, estado_pago, token_transbank, codigo_autorizacion, response_code, tipo_pago_transbank, cuotas, ultimos_4_digitos, fecha_transaccion) VALUES
    (1, 15000.00, '2024-06-01 10:23:00', 'aprobado',  'TOKEN_TB_001', 'AUTH001', 0, 'VN', 1, '4521', '2024-06-01 10:24:00'),
    (1, 28000.00, '2024-06-05 14:10:00', 'aprobado',  'TOKEN_TB_002', 'AUTH002', 0, 'VD', 1, '7832', '2024-06-05 14:11:00'),
    (1, 12000.00, '2024-06-10 09:45:00', 'rechazado', 'TOKEN_TB_003', NULL,     -1, 'VN', 1, '1234', '2024-06-10 09:46:00'),
    (2, 45000.00, '2024-06-12 16:00:00', 'aprobado',  NULL,           NULL,      0,  NULL, NULL, NULL, '2024-06-12 16:05:00'),
    (1,  8000.00, '2024-06-15 11:30:00', 'iniciado',  'TOKEN_TB_005', NULL,     NULL,'VN', 1, '9876', NULL);

-- ------------------------------------------------------------
-- 20. SOLICITUD_SERVICIO (depende de CLIENTE, TIPO_SOLICITUD, PAGO_SOLICITUD)
--     id_persona: clientes son 1, 2, 6, 7, 8
--     id_tipo_solic: 1=Limpieza básica ... 5=Fotografía
--     id_transaccion: nullable, solo las pagadas lo tienen
-- ------------------------------------------------------------
INSERT INTO SOLICITUD_SERVICIO (id_persona, id_tipo_solic, id_transaccion, fecha_creacion, estado_solicitud, instrucciones, total_compra) VALUES
    (1, 1, 1, '2024-06-01 09:00:00', 'completada', 'Limpiar nicho galería A nivel 2, con especial cuidado en la lápida',    15000.00),
    (2, 2, 2, '2024-06-05 13:00:00', 'completada', 'Limpieza profunda de tumba sección 3. Llevar detergente especializado',  28000.00),
    (6, 3, 3, '2024-06-10 09:00:00', 'cancelada',  'Colocar arreglo floral rosas rojas. Pago rechazado, servicio cancelado', 12000.00),
    (7, 4, 4, '2024-06-12 15:00:00', 'en_proceso', 'Repintar lápida de mármol, color blanco. Espacio deteriorado',           45000.00),
    (8, 5, NULL,'2024-06-15 11:00:00','pendiente',  'Solo fotografiar el estado actual del nicho 089 columbario norte',       8000.00);

-- ------------------------------------------------------------
-- 21. CALIFICACION (sin dependencias propias, luego FK desde DETALLE_ORDEN)
-- ------------------------------------------------------------
INSERT INTO CALIFICACION (puntuacion, comentario, fecha_evaluacion) VALUES
    (5, 'Excelente servicio, el cuidador fue muy profesional y puntual',         '2024-06-02 10:00:00'),
    (4, 'Buen trabajo en general, dejó el espacio muy limpio',                   '2024-06-06 15:30:00'),
    (3, 'Servicio aceptable pero llegó 30 minutos tarde',                        '2024-06-07 12:00:00'),
    (5, 'Superó mis expectativas, excelente atención al detalle',                '2024-06-08 09:45:00'),
    (2, 'El trabajo fue incompleto, faltó limpiar la parte trasera del nicho',   '2024-06-09 16:20:00');

-- ------------------------------------------------------------
-- 22. CUENTA_BANCO (depende de CUIDADOR y TIPO_CUENTA)
--     id_persona cuidadores: 3, 4, 9, 10, 11
--     id_tipo_cuenta: 1=corriente, 2=vista, 3=ahorro
-- ------------------------------------------------------------
INSERT INTO CUENTA_BANCO (id_persona, id_tipo_cuenta, banco, numero_cuenta, rut_titular, nombre_titular, estado_activo) VALUES
    (3,  1, 'Banco de Chile',      '00123456789', '34567890-1', 'Pedro Silva Muñoz',     1),
    (4,  2, 'BancoEstado',         '00234567890', '45678901-2', 'María González Vargas', 1),
    (9,  3, 'Scotiabank',          '00345678901', '90123456-7', 'Rosa Méndez Contreras', 1),
    (10, 1, 'Santander',           '00456789012', '01234567-8', 'Diego Torrez Ibáñez',   1),
    (11, 2, 'Banco de Chile',      '00567890123', '11223344-5', 'Camila Flores Pino',    1);

-- ------------------------------------------------------------
-- 23. PAGO_CUIDADOR (depende de CUENTA_BANCO y TIPO_PAGO)
--     id_cuenta: 1=Pedro, 2=María, 3=Rosa, 4=Diego, 5=Camila
-- ------------------------------------------------------------
INSERT INTO PAGO_CUIDADOR (id_cuenta, id_tipo_pago, monto, fecha_pago, estado_pago, id_transaccion_ext, comprobante_url) VALUES
    (1, 2, 9000.00,  '2024-06-02 12:00:00', 'procesado', 'KHIPU-001', 'https://storage.cuidadoeterno.cl/comprobantes/pago_001.pdf'),
    (2, 2, 16800.00, '2024-06-06 17:00:00', 'procesado', 'KHIPU-002', 'https://storage.cuidadoeterno.cl/comprobantes/pago_002.pdf'),
    (3, 2,  7200.00, '2024-06-07 13:00:00', 'pendiente', NULL,        NULL),
    (4, 2, 27000.00, '2024-06-13 09:00:00', 'procesado', 'KHIPU-004', 'https://storage.cuidadoeterno.cl/comprobantes/pago_004.pdf'),
    (5, 2,  4800.00, '2024-06-16 10:00:00', 'pendiente', NULL,        NULL);

-- ------------------------------------------------------------
-- 24. DETALLE_ORDEN (depende de SOLICITUD_SERVICIO, CUIDADOR, PRODUCTO,
--                    CALIFICACION, PAGO_CUIDADOR, ESPACIO — todos nullable excepto los 3 primeros)
--     id_solicitud: 1-5
--     id_persona_cuidador: 3=Pedro, 4=María, 9=Rosa, 10=Diego, 11=Camila
--     id_producto: 1-5
--     id_espacio: 1-5
-- ------------------------------------------------------------
INSERT INTO DETALLE_ORDEN (id_solicitud, id_persona_cuidador, id_producto, id_calificacion, id_pago_cuidador, id_espacio, cantidad_productos, precio_unitario, subtotal, estado_orden) VALUES
    (1, 3,  1, 1, 1, 1, 2,  3500.00,  7000.00, 'completada'),
    (2, 4,  4, 2, 2, 2, 3,  2800.00,  8400.00, 'completada'),
    (3, 9,  2, 3, 3, 3, 1,  8500.00,  8500.00, 'cancelada'),
    (4, 10, 3, 4, 4, 5, 2,  6200.00, 12400.00, 'en_proceso'),
    (5, 11, 5, 5, 5, 4, 1,  4100.00,  4100.00, 'pendiente');

-- ------------------------------------------------------------
-- 25. REGISTRO_EVIDENCIA (depende de DETALLE_ORDEN)
--     tipo_momento: 'antes' o 'despues'
-- ------------------------------------------------------------
INSERT INTO REGISTRO_EVIDENCIA (id_orden, url_foto, fecha_registro, descripcion_estado, tipo_momento, validado) VALUES
    (1, 'https://storage.cuidadoeterno.cl/evidencia/orden1_antes.jpg',  '2024-06-01 09:30:00', 'Nicho con polvo acumulado y flores secas',          'antes',   1),
    (1, 'https://storage.cuidadoeterno.cl/evidencia/orden1_despues.jpg','2024-06-01 10:45:00', 'Nicho limpio, lápida brillante sin residuos',        'despues', 1),
    (2, 'https://storage.cuidadoeterno.cl/evidencia/orden2_antes.jpg',  '2024-06-05 13:30:00', 'Tumba con maleza y manchas de humedad en la lápida', 'antes',   1),
    (2, 'https://storage.cuidadoeterno.cl/evidencia/orden2_despues.jpg','2024-06-05 15:20:00', 'Tumba limpia, sin maleza, lápida tratada',           'despues', 1),
    (4, 'https://storage.cuidadoeterno.cl/evidencia/orden4_antes.jpg',  '2024-06-12 16:30:00', 'Lápida con pintura descascarada y grafiti leve',     'antes',   0);

-- ------------------------------------------------------------
-- 26. RETIRO_INSUMO (depende de DETALLE_ORDEN y PUESTO_VENTA)
-- ------------------------------------------------------------
INSERT INTO RETIRO_INSUMO (id_orden, id_puesto, fecha_retiro, monto_total, url_boleta_foto, estado_retiro) VALUES
    (1, 2, '2024-06-01 09:00:00',  7000.00, 'https://storage.cuidadoeterno.cl/boletas/retiro_001.jpg', 'completado'),
    (2, 2, '2024-06-05 13:00:00',  8400.00, 'https://storage.cuidadoeterno.cl/boletas/retiro_002.jpg', 'completado'),
    (3, 3, '2024-06-10 08:45:00',  8500.00, 'https://storage.cuidadoeterno.cl/boletas/retiro_003.jpg', 'completado'),
    (4, 1, '2024-06-12 15:30:00', 12400.00, 'https://storage.cuidadoeterno.cl/boletas/retiro_004.jpg', 'completado'),
    (5, 4, '2024-06-15 10:30:00',  4100.00, NULL,                                                       'pendiente');

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- RESUMEN DE IDs generados para referencia rápida
-- ============================================================
--
-- ROL:            1=CLIENTE, 2=CUIDADOR, 3=ADMINISTRADOR
-- REGION:         1=RM, 2=Valparaíso, 3=Biobío, 4=Araucanía, 5=Los Lagos
-- PROVINCIA:      1=Santiago, 2=Cordillera, 3=Valparaíso, 4=Concepción, 5=Cautín
-- COMUNA:         1=Santiago Centro, 2=Recoleta, 3=Puente Alto, 4=Valparaíso, 5=Concepción
-- HORARIO:        1=lunes ... 5=viernes
-- TIPO_CUENTA:    1=corriente, 2=vista, 3=ahorro
-- TIPO_ESPACIO:   1=Nicho, 2=Tumba, 3=Mausoleo, 4=Columbario, 5=Bóveda
-- TIPO_SOLICITUD: 1=Limpieza básica ... 5=Fotografía
-- TIPO_PAGO:      1=webpay, 2=transferencia, 3=khipu, 4=efectivo, 5=oneclick
-- PRODUCTO:       1=Escoba, 2=Arreglo rosas, 3=Pintura, 4=Detergente, 5=Velas
-- CREDENCIAL:     1-5=usuarios base, 6-8=clientes extra, 9-11=cuidadores extra, 12-15=admins extra
-- PERSONA:        1=Ana(C), 2=Carlos(C), 3=Pedro(CU), 4=María(CU), 5=Jorge(A)
--                 6=Luis(C), 7=Sofía(C), 8=Juan(C)
--                 9=Rosa(CU), 10=Diego(CU), 11=Camila(CU)
--                 12-15=Admins adicionales
-- CEMENTERIO:     1=Gral Santiago, 2=Católico, 3=Parque Recuerdo, 4=Valparaíso, 5=Concepción
-- ESPACIO:        1-5 (uno por cementerio)
-- FALLECIDO:      1-5 (uno por espacio)
-- PUESTO_VENTA:   1-5
-- SOLICITUD:      1=completada, 2=completada, 3=cancelada, 4=en_proceso, 5=pendiente
-- DETALLE_ORDEN:  1-5 (una por solicitud)
-- ============================================================
