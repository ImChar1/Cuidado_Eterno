-- ============================================================
-- CUIDADO ETERNO — seed_data.sql
-- Datos de prueba para desarrollo local
--
-- IMPORTANTE — CREDENCIAL Y PERSONAS (CLIENTE / CUIDADOR):
--   Las filas de CREDENCIAL, PERSONA, CLIENTE y CUIDADOR NO 
--   se insertan en este script para no romper el flujo de 
--   seguridad PBKDF2-HMAC-SHA256 de Spring Security.
--
--   Procedimiento para poblar usuarios cliente/cuidador:
--     1. Levantar el proyecto.
--     2. Usar POST /api/v1/auth/registro/cliente o /registro/cuidador
--        para crear cada usuario desde Swagger.
--
--   EXCEPCION — ADMINISTRADOR:
--     Como no existe (o no se expone) un endpoint público de
--     registro de administrador, al final de este script se
--     agrega un bloque OPCIONAL para crear un usuario admin
--     directamente por SQL. Lee las notas de esa sección antes
--     de ejecutarlo: el hash de ejemplo puede NO calzar con el
--     PasswordEncoder real de tu proyecto.
--
-- ORDEN DE EJECUCION:
--   Este script asume que cuidado_eterno_mariadb_DDL.sql ya corrió
--   y que las tablas existen y están vacías.
-- ============================================================
 
SET FOREIGN_KEY_CHECKS = 0;
USE cuidado_eterno;
 
-- ============================================================
-- 1. ROL
-- ============================================================
INSERT INTO rol (nombre_rol) VALUES
    ('CLIENTE'),
    ('CUIDADOR'),
    ('ADMINISTRADOR');
 
-- ============================================================
-- 2. REGION
-- ============================================================
INSERT INTO region (nombre_region) VALUES
    ('Región Metropolitana de Santiago'),
    ('Región de Valparaíso'),
    ('Región del Biobío'),
    ('Región de La Araucanía'),
    ('Región de Los Lagos');
 
-- ============================================================
-- 3. PROVINCIA (depende de REGION)
--    id_region: 1=RM, 2=Valparaíso, 3=Biobío, 4=Araucanía, 5=Los Lagos
-- ============================================================
INSERT INTO provincia (nombre_provincia, id_region) VALUES
    ('Santiago',    1),
    ('Cordillera',  1),
    ('Valparaíso',  2),
    ('Concepción',  3),
    ('Cautín',      4);
 
-- ============================================================
-- 4. COMUNA (depende de PROVINCIA)
--    id_provincia: 1=Santiago, 2=Cordillera, 3=Valparaíso, 4=Concepción, 5=Cautín
-- ============================================================
INSERT INTO comuna (nombre_comuna, id_provincia) VALUES
    ('Santiago Centro',  1),
    ('Recoleta',         1),
    ('Puente Alto',      2),
    ('Valparaíso',       3),
    ('Concepción',       4);
 
-- ============================================================
-- 5. HORARIO (sin dependencias)
-- ============================================================
INSERT INTO horario (dia_semana, hora_inicio, hora_fin, estado_disponibilidad) VALUES
    ('lunes',     '08:00:00', '18:00:00', 1),
    ('martes',    '08:00:00', '18:00:00', 1),
    ('miercoles', '08:00:00', '18:00:00', 1),
    ('jueves',    '08:00:00', '18:00:00', 1),
    ('viernes',   '08:00:00', '17:00:00', 1);
 
-- ============================================================
-- 6. TIPO_CUENTA (sin dependencias)
-- ============================================================
INSERT INTO tipo_cuenta (nombre_tipo) VALUES
    ('corriente'),
    ('vista'),
    ('ahorro');
 
-- ============================================================
-- 7. TIPO_ESPACIO (sin dependencias)
--    nivel_complejidad: 1=básico, 2=intermedio, 3=alto
-- ============================================================
INSERT INTO tipo_espacio (nombre_tipo, descripcion, nivel_complejidad) VALUES
    ('Nicho',      'Espacio individual en muro de nichos, acceso frontal',            1),
    ('Tumba',      'Espacio en tierra con lápida, requiere herramientas especiales',  2),
    ('Mausoleo',   'Estructura familiar de gran tamaño, varios niveles internos',     3),
    ('Columbario', 'Urna para cenizas, espacio reducido de fácil acceso',             1),
    ('Bóveda',     'Construcción subterránea familiar, alta complejidad de acceso',   3);
 
-- ============================================================
-- 8. TIPO_SOLICITUD (sin dependencias)
--    precio_base en CLP, duracion_estimada_min en minutos
-- ============================================================
INSERT INTO tipo_solicitud (nombre_servicio, descripcion, precio_base, duracion_estimada_min, requiere_insumos, estado_sv) VALUES
    ('Mantenimiento', 'Limpieza general, retiro de escombros y reparaciones menores del espacio', 25000.00, 60, 1, 1),
    ('Jardinería', 'Corte de pasto, poda de arbustos, riego y cuidado de plantas en la sepultura', 18000.00, 45, 1, 1),
    ('Ornato y Conmemoración', 'Limpieza profunda, pulido de lápidas y colocación de arreglos florales', 35000.00, 90, 1, 1);
 
-- ============================================================
-- 9. TIPO_PAGO (sin dependencias)
-- ============================================================
INSERT INTO tipo_pago (nombre_metodo, activo) VALUES
    ('Webpay',        1),
    ('transferencia', 1),
    ('Khipu',         1),
    ('efectivo',      1),
    ('oneclick',      1);
 
-- ============================================================
-- 10. PRODUCTO (sin dependencias)
--     Se amplió el catálogo con 10 productos adicionales (ids 6-15)
-- ============================================================
INSERT INTO producto (nombre, descripcion, categoria, precio_costo, url_imagen, estado_activo) VALUES
    ('Escoba industrial',    'Escoba de cerdas duras para superficies de cemento',          'herramienta', 3500.00, 'https://storage.cuidadoeterno.cl/productos/escoba.jpg',     1),
    ('Arreglo floral rosas', 'Arreglo de 12 rosas rojas con follaje verde',                 'floral',      8500.00, 'https://storage.cuidadoeterno.cl/productos/rosas.jpg',      1),
    ('Pintura blanca 1L',    'Pintura látex blanca resistente a humedad y rayos UV',        'pintura',     6200.00, 'https://storage.cuidadoeterno.cl/productos/pintura.jpg',    1),
    ('Detergente multiusos', 'Detergente concentrado para limpieza de mármol y granito',    'limpieza',    2800.00, 'https://storage.cuidadoeterno.cl/productos/detergente.jpg', 1),
    ('Velas aromáticas x3',  'Set de 3 velas blancas aromáticas de larga duración 8 horas', 'ceremonial',  4100.00, 'https://storage.cuidadoeterno.cl/productos/velas.jpg',      1),
    -- --- 10 productos nuevos (ids 6-15) ---
    ('Cepillo de acero inoxidable', 'Cepillo de cerdas de acero para remover musgo y sarro de piedra y mármol', 'herramienta', 4200.00, 'https://storage.cuidadoeterno.cl/productos/cepillo_acero.jpg',       1),
    ('Arreglo floral claveles',     'Arreglo de 10 claveles blancos con helechos decorativos',                  'floral',      7200.00, 'https://storage.cuidadoeterno.cl/productos/claveles.jpg',            1),
    ('Barniz protector mármol',     'Barniz transparente que protege la piedra de la humedad y el desgaste',   'pintura',     9500.00, 'https://storage.cuidadoeterno.cl/productos/barniz.jpg',              1),
    ('Limpiavidrios profesional 1L','Limpiavidrios concentrado para lápidas y superficies vítreas',             'limpieza',    3100.00, 'https://storage.cuidadoeterno.cl/productos/limpiavidrios.jpg',       1),
    ('Set velas votivas x6',        'Set de 6 velas votivas pequeñas para ofrendas y conmemoraciones',          'ceremonial',  3800.00, 'https://storage.cuidadoeterno.cl/productos/velas_votivas.jpg',       1),
    ('Pala de jardín pequeña',      'Pala manual de acero para remoción de tierra y plantas pequeñas',          'herramienta', 5600.00, 'https://storage.cuidadoeterno.cl/productos/pala_jardin.jpg',         1),
    ('Ramo de flores mixtas',       'Ramo surtido de flores de temporada con papel decorativo',                 'floral',      6500.00, 'https://storage.cuidadoeterno.cl/productos/ramo_mixto.jpg',          1),
    ('Sellador impermeabilizante',  'Sellador transparente que repele el agua y evita manchas de humedad',      'pintura',     11000.00, 'https://storage.cuidadoeterno.cl/productos/sellador.jpg',           1),
    ('Desinfectante multiusos 1L',  'Desinfectante bactericida para superficies de piedra, granito y cemento', 'limpieza',    3300.00, 'https://storage.cuidadoeterno.cl/productos/desinfectante.jpg',       1),
    ('Cruz conmemorativa de madera','Cruz decorativa de madera tallada para colocar en nichos y tumbas',        'ceremonial',  5200.00, 'https://storage.cuidadoeterno.cl/productos/cruz_madera.jpg',         1);
 
-- ============================================================
-- 11. CEMENTERIO (depende de COMUNA y HORARIO)
--     id_comuna: 1=Santiago Centro, 2=Recoleta, 3=Puente Alto,
--                4=Valparaíso, 5=Concepción
--
--     Todos los recintos son cementerios PUBLICOS/municipales o
--     parroquiales tradicionales, ubicados en Chile. Se excluyó
--     deliberadamente cualquier "cementerio parque" (cadena
--     privada tipo memorial park).
-- ============================================================
INSERT INTO cementerio (id_comuna, id_horario, nombre_cementerio, direccion, latitud, longitud) VALUES
    (1, 1, 'Cementerio General de Santiago',     'Av. Profesor Alberto Zañartu 951, Recoleta',                 -33.4262300, -70.6631400),
    (2, 2, 'Cementerio Católico de Santiago',    'Av. Brasil 1001, Recoleta',                                  -33.4197800, -70.6623900),
    (3, 3, 'Cementerio Católico Bajos de Mena',  'Av. Eyzaguirre 2337, Puente Alto',                           -33.5975000, -70.5754000),
    (4, 4, 'Cementerio N°3 de Playa Ancha',      'Subida Cementerio S/N, Playa Ancha, Valparaíso',             -33.0375000, -71.6320000),
    (5, 5, 'Cementerio General de Concepción',   'Av. Rodolfo Briceño 1995, Sector Lorenzo Arenas, Concepción',-36.8280000, -73.0430000);
 
-- ============================================================
-- 12. ESPACIO (depende de TIPO_ESPACIO y CEMENTERIO)
--     Se actualizó a sector_pabellon, numero_sepultura y coordenadas exactas
-- ============================================================
INSERT INTO espacio (id_tipo_espacio, id_cementerio, sector_pabellon, numero_sepultura, coordenada_latitud, coordenada_longitud, material_principal, estado_fisico, url_foto_referencia) VALUES
    (1, 1, 'Galería A, Nivel 2',    'Nicho 145', -33.42623050, -70.66314050, 'marmol',  'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_1.jpg'),
    (2, 1, 'Sección 3, Fila 8',     'Tumba 22',  -33.42623100, -70.66314100, 'granito', 'regular',     'https://storage.cuidadoeterno.cl/espacios/espacio_2.jpg'),
    (3, 2, 'Mausoleo Familiar',     'Bloque M04',-33.41978050, -70.66239050, 'cemento', 'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_3.jpg'),
    (4, 3, 'Columbario Norte',      'Urna 089',  -33.59750500, -70.57540500, 'madera',  'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_4.jpg'),
    (5, 4, 'Sector B',              'Lote 17',   -33.03750500, -71.63200500, 'piedra',  'deteriorado', 'https://storage.cuidadoeterno.cl/espacios/espacio_5.jpg');
 
-- ============================================================
-- 13. FALLECIDO (depende de ESPACIO)
--     Se removió rut_fallecido conforme al nuevo DDL
-- ============================================================
INSERT INTO fallecido (id_espacio, nombres, apellidos, fecha_nacimiento, fecha_defuncion, epitafio) VALUES
    (1, 'Manuel Eduardo',   'Soto Ramírez',    '1940-05-12', '2020-11-03', 'Amado esposo y padre, siempre en nuestros corazones'),
    (2, 'Carmen Rosa',      'Díaz Moreno',     '1935-09-28', '2019-06-15', 'Tu amor nos guía eternamente'),
    (3, 'Roberto Andrés',   'Vega Castillo',   '1928-01-07', '2021-03-22', 'Descansa en paz, buen hombre'),
    (4, 'Elena Beatriz',    'Fuentes Alarcón', '1952-12-18', '2022-08-10', NULL),
    (5, 'Francisco Javier', 'Muñoz Torres',    '1945-04-03', '2023-01-28', 'En el recuerdo vive quien amamos');
 
-- ============================================================
-- 14. PUESTO_VENTA (depende de CEMENTERIO)
--     Se agregó un puesto para el Cementerio General de Concepción
--     (id_cementerio 5), que antes no tenía ningún local asociado.
-- ============================================================
INSERT INTO puesto_venta (id_cementerio, nombre_local, ubicacion_ref, telefono, estado_puesto) VALUES
    (1, 'Florería El Ángel',          'Entrada principal, lado derecho, local 3',     '+56922111333', 1),
    (1, 'Insumos Cementerio Norte',   'Galería B, frente a administración',           '+56933222444', 1),
    (2, 'Flores y Velas Santa Rosa',  'Acceso sur, kiosco 2',                         '+56944333555', 1),
    (3, 'Todo para el Recuerdo',      'Estacionamiento, módulo prefabricado azul',    '+56955444666', 1),
    (4, 'Bazar Porteño Memorias',     'Portería principal, frente al mapa del parque','+56966555777', 1),
    (5, 'Recordatorios del Bío Bío',  'Acceso principal, junto a la administración',  '+56977666888', 1);
 
-- ============================================================
-- 15. CATALOGO_PRODUCTO (depende de PUESTO_VENTA y PRODUCTO)
--     Se agregaron las asignaciones de los 10 productos nuevos
--     a los puestos de venta existentes y al nuevo puesto de
--     Concepción. Cada producto nuevo quedó disponible en al
--     menos un puesto.
-- ============================================================
INSERT INTO catalogo_producto (id_puesto, id_producto, precio_venta, hay_stock) VALUES
    (1, 2, 12500.00, 1),  -- Florería El Ángel: arreglo floral rosas
    (1, 5,  6200.00, 1),  -- Florería El Ángel: velas aromáticas
    (2, 1,  4800.00, 1),  -- Insumos Norte: escoba industrial
    (2, 4,  3500.00, 1),  -- Insumos Norte: detergente
    (3, 3,  7900.00, 0),  -- Flores Santa Rosa: pintura (sin stock)
    -- --- asignaciones nuevas ---
    (1, 7,  9800.00, 1),  -- Florería El Ángel: arreglo floral claveles
    (1, 12, 8900.00, 1),  -- Florería El Ángel: ramo de flores mixtas
    (1, 15, 7500.00, 1),  -- Florería El Ángel: cruz conmemorativa de madera
    (2, 6,  5900.00, 1),  -- Insumos Norte: cepillo de acero inoxidable
    (2, 9,  4200.00, 1),  -- Insumos Norte: limpiavidrios profesional
    (2, 8, 13500.00, 1),  -- Insumos Norte: barniz protector mármol
    (3, 10, 5200.00, 1),  -- Flores y Velas Santa Rosa: set velas votivas
    (3, 7,  9600.00, 1),  -- Flores y Velas Santa Rosa: arreglo floral claveles
    (4, 6,  6100.00, 1),  -- Todo para el Recuerdo: cepillo de acero inoxidable
    (4, 9,  4300.00, 1),  -- Todo para el Recuerdo: limpiavidrios profesional
    (4, 11, 7800.00, 1),  -- Todo para el Recuerdo: pala de jardín pequeña
    (4, 13, 15500.00, 1), -- Todo para el Recuerdo: sellador impermeabilizante
    (4, 14, 4600.00, 1),  -- Todo para el Recuerdo: desinfectante multiusos
    (5, 8, 13900.00, 1),  -- Bazar Porteño Memorias: barniz protector mármol
    (5, 10, 5400.00, 1),  -- Bazar Porteño Memorias: set velas votivas
    (5, 15, 7700.00, 1),  -- Bazar Porteño Memorias: cruz conmemorativa de madera
    (6, 9,  4400.00, 1),  -- Recordatorios del Bío Bío: limpiavidrios profesional
    (6, 11, 8000.00, 1),  -- Recordatorios del Bío Bío: pala de jardín pequeña
    (6, 13, 15800.00, 1), -- Recordatorios del Bío Bío: sellador impermeabilizante
    (6, 14, 4700.00, 1),  -- Recordatorios del Bío Bío: desinfectante multiusos
    (6, 7,  9700.00, 1);  -- Recordatorios del Bío Bío: arreglo floral claveles
 
SET FOREIGN_KEY_CHECKS = 1;
 
-- ============================================================
-- 16. ADMINISTRADOR
--     Hash PBKDF2-HMAC-SHA256 generado con los parámetros reales
--     del proyecto (Pbkdf2PasswordEncoder("", 16, 310_000,
--     PBKDF2WithHmacSHA256), sin prefijo {pbkdf2}, salt 16 bytes,
--     hash 256 bits, formato hex(salt || hash) = 96 caracteres).
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO credencial (nombre_usuario, clave_hash, estado_cuenta, id_rol) VALUES
    ('admin', '06ca1d43a76d05f3864e1b534323ea1e2cde6a8fe3ac33c712d2d9c9347840f9c20124799990119ebb510fb7cf2ac1a0', 'activa', 3);

INSERT INTO persona (rut, nombre, ap_paterno, ap_materno, email, telefono, fecha_nacimiento, genero, id_credencial) VALUES
    ('11111111-1', 'Admin', 'Sistema', 'CuidadoEterno', 'admin@cuidadoeterno.cl', '+56900000000', '1990-01-01', 'M',
        (SELECT id_credencial FROM credencial WHERE nombre_usuario = 'admin'));

INSERT INTO administrador (id_persona, nivel_acceso, cargo, fecha_ingreso) VALUES
    ((SELECT id_persona FROM persona WHERE email = 'admin@cuidadoeterno.cl'), 'total', 'Administrador General', CURDATE());

SET FOREIGN_KEY_CHECKS = 1;

-- Login de prueba:
--   usuario: admin
--   clave:   Admin123!
