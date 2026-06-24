-- ============================================================
-- CUIDADO ETERNO — seed_data.sql
-- Datos de prueba para desarrollo local
--
-- IMPORTANTE — CREDENCIAL Y PERSONAS:
--   Las filas de CREDENCIAL, PERSONA, CLIENTE y CUIDADOR NO 
--   se insertan en este script para no romper el flujo de 
--   seguridad PBKDF2-HMAC-SHA256 de Spring Security.
--
--   Procedimiento para poblar usuarios:
--     1. Levantar el proyecto.
--     2. Usar POST /api/v1/auth/registro/cliente o /registro/cuidador
--        para crear cada usuario desde Swagger.
--
-- ORDEN DE EJECUCION:
--   Este script asume que cuidado_eterno_mysql_DDL.sql ya corrió
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
    ('Limpieza básica',       'Limpieza superficial del espacio: retiro de polvo y desechos',        15000.00,  45, 1, 1),
    ('Limpieza profunda',     'Limpieza completa con productos especializados y cepillado',           28000.00,  90, 1, 1),
    ('Ofrenda floral',        'Colocación de arreglo floral fresco en el espacio indicado',           12000.00,  20, 1, 1),
    ('Pintura y restauración','Repintado de lápida o nicho con pintura resistente a la intemperie',   45000.00, 120, 1, 1),
    ('Fotografía de estado',  'Registro fotográfico del estado actual del espacio sin intervención',   8000.00,  15, 0, 1);

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
-- ============================================================
INSERT INTO producto (nombre, descripcion, categoria, precio_costo, url_imagen, estado_activo) VALUES
    ('Escoba industrial',    'Escoba de cerdas duras para superficies de cemento',          'herramienta', 3500.00, 'https://storage.cuidadoeterno.cl/productos/escoba.jpg',     1),
    ('Arreglo floral rosas', 'Arreglo de 12 rosas rojas con follaje verde',                 'floral',      8500.00, 'https://storage.cuidadoeterno.cl/productos/rosas.jpg',      1),
    ('Pintura blanca 1L',    'Pintura látex blanca resistente a humedad y rayos UV',        'pintura',     6200.00, 'https://storage.cuidadoeterno.cl/productos/pintura.jpg',    1),
    ('Detergente multiusos', 'Detergente concentrado para limpieza de mármol y granito',    'limpieza',    2800.00, 'https://storage.cuidadoeterno.cl/productos/detergente.jpg', 1),
    ('Velas aromáticas x3',  'Set de 3 velas blancas aromáticas de larga duración 8 horas', 'ceremonial',  4100.00, 'https://storage.cuidadoeterno.cl/productos/velas.jpg',      1);

-- ============================================================
-- 11. CEMENTERIO (depende de COMUNA y HORARIO)
--     id_comuna: 1=Santiago Centro, 2=Recoleta, 3=Puente Alto,
--                4=Valparaíso, 5=Concepción
-- ============================================================
INSERT INTO cementerio (id_comuna, id_horario, nombre_cementerio, direccion, latitud, longitud) VALUES
    (1, 1, 'Cementerio General de Santiago',    'Av. Profesor Alberto Zañartu 951, Recoleta',       -33.4262300, -70.6631400),
    (2, 2, 'Cementerio Católico de Santiago',   'Av. Brasil 1001, Recoleta',                        -33.4197800, -70.6623900),
    (3, 3, 'Cementerio Parque del Recuerdo',    'Av. Américo Vespucio 86, Vitacura',                -33.3756500, -70.5763200),
    (4, 4, 'Cementerio Municipal de Valparaíso','Av. Ecuador 1000, Valparaíso',                     -33.0419800, -71.6148700),
    (5, 5, 'Cementerio Parque Huerto del Alma', 'Autopista Concepción-Talcahuano Km 5, Concepción', -36.8100000, -73.0500000);

-- ============================================================
-- 12. ESPACIO (depende de TIPO_ESPACIO y CEMENTERIO)
--     Se actualizó a sector_pabellon, numero_sepultura y coordenadas exactas
-- ============================================================
INSERT INTO espacio (id_tipo_espacio, id_cementerio, sector_pabellon, numero_sepultura, coordenada_latitud, coordenada_longitud, material_principal, estado_fisico, url_foto_referencia) VALUES
    (1, 1, 'Galería A, Nivel 2',    'Nicho 145', -33.42623050, -70.66314050, 'marmol',  'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_1.jpg'),
    (2, 1, 'Sección 3, Fila 8',     'Tumba 22',  -33.42623100, -70.66314100, 'granito', 'regular',     'https://storage.cuidadoeterno.cl/espacios/espacio_2.jpg'),
    (3, 2, 'Mausoleo Familiar',     'Bloque M04',-33.41978050, -70.66239050, 'cemento', 'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_3.jpg'),
    (4, 3, 'Columbario Norte',      'Urna 089',  -33.37565050, -70.57632050, 'madera',  'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_4.jpg'),
    (5, 4, 'Sector B',              'Lote 17',   -33.04198050, -71.61487050, 'piedra',  'deteriorado', 'https://storage.cuidadoeterno.cl/espacios/espacio_5.jpg');

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
-- ============================================================
INSERT INTO puesto_venta (id_cementerio, nombre_local, ubicacion_ref, telefono, estado_puesto) VALUES
    (1, 'Florería El Ángel',          'Entrada principal, lado derecho, local 3',     '+56922111333', 1),
    (1, 'Insumos Cementerio Norte',   'Galería B, frente a administración',           '+56933222444', 1),
    (2, 'Flores y Velas Santa Rosa',  'Acceso sur, kiosco 2',                         '+56944333555', 1),
    (3, 'Todo para el Recuerdo',      'Estacionamiento, módulo prefabricado azul',    '+56955444666', 1),
    (4, 'Bazar Porteño Memorias',     'Portería principal, frente al mapa del parque','+56966555777', 1);

-- ============================================================
-- 15. CATALOGO_PRODUCTO (depende de PUESTO_VENTA y PRODUCTO)
-- ============================================================
INSERT INTO catalogo_producto (id_puesto, id_producto, precio_venta, hay_stock) VALUES
    (1, 2, 12500.00, 1),  -- Florería El Ángel: arreglo floral rosas
    (1, 5,  6200.00, 1),  -- Florería El Ángel: velas aromáticas
    (2, 1,  4800.00, 1),  -- Insumos Norte: escoba industrial
    (2, 4,  3500.00, 1),  -- Insumos Norte: detergente
    (3, 3,  7900.00, 0);  -- Flores Santa Rosa: pintura (sin stock)

SET FOREIGN_KEY_CHECKS = 1;