-- ============================================================
-- CUIDADO ETERNO — seed_data.sql
-- Datos de prueba para desarrollo local
--
-- IMPORTANTE — CREDENCIAL:
--   Las filas de CREDENCIAL NO se insertan en este script.
--   clave_hash usa PBKDF2-HMAC-SHA256 generado por Spring Security,
--   cuyo formato no puede generarse manualmente sin el encoder de Java.
--
--   Procedimiento para poblar CREDENCIAL:
--     1. Levantar el proyecto: docker compose up --build -d
--     2. Usar POST /api/v1/auth/registro/cliente o /registro/cuidador
--        para crear cada usuario desde Swagger o Postman.
--     3. Spring genera el hash correcto y lo persiste automáticamente.
--     4. Verificar con: docker exec -it cuidado_eterno_db
--          mariadb -u ce_user -pce_pass cuidado_eterno
--          -e "SELECT nombre_usuario, LEFT(clave_hash,20) FROM CREDENCIAL;"
--
--   Una vez creados los usuarios via API, el resto de este seed
--   puede ejecutarse para poblar las tablas de negocio.
--
-- ORDEN DE EJECUCION:
--   Este script asume que cuidado_eterno_mysql.sql ya corrió
--   y que las tablas existen y están vacías.
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;
USE cuidado_eterno;

-- ============================================================
-- 1. ROL
-- ============================================================
INSERT INTO ROL (nombre_rol) VALUES
    ('CLIENTE'),
    ('CUIDADOR'),
    ('ADMINISTRADOR');

-- ============================================================
-- 2. REGION
-- ============================================================
INSERT INTO REGION (nombre_region) VALUES
    ('Región Metropolitana de Santiago'),
    ('Región de Valparaíso'),
    ('Región del Biobío'),
    ('Región de La Araucanía'),
    ('Región de Los Lagos');

-- ============================================================
-- 3. PROVINCIA (depende de REGION)
--    id_region: 1=RM, 2=Valparaíso, 3=Biobío, 4=Araucanía, 5=Los Lagos
-- ============================================================
INSERT INTO PROVINCIA (nombre_provincia, id_region) VALUES
    ('Santiago',    1),
    ('Cordillera',  1),
    ('Valparaíso',  2),
    ('Concepción',  3),
    ('Cautín',      4);

-- ============================================================
-- 4. COMUNA (depende de PROVINCIA)
--    id_provincia: 1=Santiago, 2=Cordillera, 3=Valparaíso, 4=Concepción, 5=Cautín
-- ============================================================
INSERT INTO COMUNA (nombre_comuna, id_provincia) VALUES
    ('Santiago Centro',  1),
    ('Recoleta',         1),
    ('Puente Alto',      2),
    ('Valparaíso',       3),
    ('Concepción',       4);

-- ============================================================
-- 5. HORARIO (sin dependencias)
-- ============================================================
INSERT INTO HORARIO (dia_semana, hora_inicio, hora_fin, estado_disponibilidad) VALUES
    ('lunes',     '08:00:00', '18:00:00', 1),
    ('martes',    '08:00:00', '18:00:00', 1),
    ('miercoles', '08:00:00', '18:00:00', 1),
    ('jueves',    '08:00:00', '18:00:00', 1),
    ('viernes',   '08:00:00', '17:00:00', 1);

-- ============================================================
-- 6. TIPO_CUENTA (sin dependencias)
-- ============================================================
INSERT INTO TIPO_CUENTA (nombre_tipo) VALUES
    ('corriente'),
    ('vista'),
    ('ahorro');

-- ============================================================
-- 7. TIPO_ESPACIO (sin dependencias)
--    nivel_complejidad: 1=básico, 2=intermedio, 3=alto
-- ============================================================
INSERT INTO TIPO_ESPACIO (nombre_tipo, descripcion, nivel_complejidad) VALUES
    ('Nicho',      'Espacio individual en muro de nichos, acceso frontal',            1),
    ('Tumba',      'Espacio en tierra con lápida, requiere herramientas especiales',  2),
    ('Mausoleo',   'Estructura familiar de gran tamaño, varios niveles internos',     3),
    ('Columbario', 'Urna para cenizas, espacio reducido de fácil acceso',             1),
    ('Bóveda',     'Construcción subterránea familiar, alta complejidad de acceso',   3);

-- ============================================================
-- 8. TIPO_SOLICITUD (sin dependencias)
--    precio_base en CLP, duracion_estimada_min en minutos
-- ============================================================
INSERT INTO TIPO_SOLICITUD (nombre_servicio, descripcion, precio_base, duracion_estimada_min, requiere_insumos, estado_sv) VALUES
    ('Limpieza básica',       'Limpieza superficial del espacio: retiro de polvo y desechos',        15000.00,  45, 1, 1),
    ('Limpieza profunda',     'Limpieza completa con productos especializados y cepillado',           28000.00,  90, 1, 1),
    ('Ofrenda floral',        'Colocación de arreglo floral fresco en el espacio indicado',           12000.00,  20, 1, 1),
    ('Pintura y restauración','Repintado de lápida o nicho con pintura resistente a la intemperie',   45000.00, 120, 1, 1),
    ('Fotografía de estado',  'Registro fotográfico del estado actual del espacio sin intervención',   8000.00,  15, 0, 1);

-- ============================================================
-- 9. TIPO_PAGO (sin dependencias)
-- ============================================================
INSERT INTO TIPO_PAGO (nombre_metodo, activo) VALUES
    ('Webpay',        1),
    ('transferencia', 1),
    ('Khipu',         1),
    ('efectivo',      1),
    ('oneclick',      1);

-- ============================================================
-- 10. PRODUCTO (sin dependencias)
-- ============================================================
INSERT INTO PRODUCTO (nombre, descripcion, categoria, precio_costo, url_imagen, estado_activo) VALUES
    ('Escoba industrial',    'Escoba de cerdas duras para superficies de cemento',          'herramienta', 3500.00, 'https://storage.cuidadoeterno.cl/productos/escoba.jpg',     1),
    ('Arreglo floral rosas', 'Arreglo de 12 rosas rojas con follaje verde',                 'floral',      8500.00, 'https://storage.cuidadoeterno.cl/productos/rosas.jpg',      1),
    ('Pintura blanca 1L',    'Pintura látex blanca resistente a humedad y rayos UV',        'pintura',     6200.00, 'https://storage.cuidadoeterno.cl/productos/pintura.jpg',    1),
    ('Detergente multiusos', 'Detergente concentrado para limpieza de mármol y granito',    'limpieza',    2800.00, 'https://storage.cuidadoeterno.cl/productos/detergente.jpg', 1),
    ('Velas aromáticas x3',  'Set de 3 velas blancas aromáticas de larga duración 8 horas','ceremonial',  4100.00, 'https://storage.cuidadoeterno.cl/productos/velas.jpg',      1);

-- ============================================================
-- PAUSA — CREAR USUARIOS VÍA API ANTES DE CONTINUAR
-- ============================================================
--
-- Antes de ejecutar el resto del seed, debes crear los usuarios
-- usando los endpoints de registro para que Spring genere los
-- hashes PBKDF2-HMAC-SHA256 correctamente.
--
-- Usuarios a crear vía POST /api/v1/auth/registro/cliente:
--
--   { "rut":"12345678-9", "nombre":"Ana", "apPaterno":"Martínez",
--     "apMaterno":"López", "email":"ana.martinez@email.com",
--     "telefono":"+56912345678", "fechaNacimiento":"1990-03-15",
--     "genero":"F", "nombreUsuario":"ana.martinez",
--     "clave":"Test1234!", "prefNotificacion":"push" }
--
--   { "rut":"23456789-0", "nombre":"Carlos", "apPaterno":"Rojas",
--     "apMaterno":"Fuentes", "email":"carlos.rojas@email.com",
--     "telefono":"+56923456789", "fechaNacimiento":"1985-07-22",
--     "genero":"M", "nombreUsuario":"carlos.rojas",
--     "clave":"Test1234!", "prefNotificacion":"email" }
--
-- Usuarios a crear vía POST /api/v1/auth/registro/cuidador
-- (requiere token de ADMINISTRADOR en Authorization: Bearer <token>):
--
--   { "rut":"34567890-1", "nombre":"Pedro", "apPaterno":"Silva",
--     "apMaterno":"Muñoz", "email":"pedro.silva@email.com",
--     "telefono":"+56934567890", "fechaNacimiento":"1988-11-08",
--     "genero":"M", "nombreUsuario":"pedro.silva",
--     "clave":"Test1234!", "idHorario":1 }
--
--   { "rut":"45678901-2", "nombre":"María", "apPaterno":"González",
--     "apMaterno":"Vargas", "email":"maria.gonzalez@email.com",
--     "telefono":"+56945678901", "fechaNacimiento":"1992-05-30",
--     "genero":"F", "nombreUsuario":"maria.gonzalez",
--     "clave":"Test1234!", "idHorario":2 }
--
-- El ADMINISTRADOR inicial debe crearse directamente en BD
-- una sola vez, insertando en CREDENCIAL/PERSONA/ADMINISTRADOR
-- con el hash generado por la API.
-- Ver sección "ADMINISTRADOR INICIAL" al final de este archivo.
--
-- Una vez creados todos los usuarios via API, continúa
-- ejecutando el resto de este script desde aquí hacia abajo.
-- ============================================================

-- ============================================================
-- 11. CEMENTERIO (depende de COMUNA y HORARIO)
--     id_comuna: 1=Santiago Centro, 2=Recoleta, 3=Puente Alto,
--                4=Valparaíso, 5=Concepción
--     id_horario: 1-5 (lunes a viernes)
-- ============================================================
INSERT INTO CEMENTERIO (id_comuna, id_horario, nombre_cementerio, direccion, latitud, longitud) VALUES
    (1, 1, 'Cementerio General de Santiago',    'Av. Profesor Alberto Zañartu 951, Recoleta',       -33.42623, -70.66314),
    (2, 2, 'Cementerio Católico de Santiago',   'Av. Brasil 1001, Recoleta',                        -33.41978, -70.66239),
    (3, 3, 'Cementerio Parque del Recuerdo',    'Av. Américo Vespucio 86, Vitacura',                -33.37565, -70.57632),
    (4, 4, 'Cementerio Municipal de Valparaíso','Av. Ecuador 1000, Valparaíso',                     -33.04198, -71.61487),
    (5, 5, 'Cementerio Parque Huerto del Alma', 'Autopista Concepción-Talcahuano Km 5, Concepción', -36.81000, -73.05000);

-- ============================================================
-- 12. ESPACIO (depende de TIPO_ESPACIO y CEMENTERIO)
--     id_tipo_espacio: 1=Nicho, 2=Tumba, 3=Mausoleo, 4=Columbario, 5=Bóveda
--     estado_fisico: 'bueno', 'regular', 'deteriorado'
-- ============================================================
INSERT INTO ESPACIO (id_tipo_espacio, id_cementerio, ubicacion_interna, material_principal, estado_fisico, url_foto_referencia) VALUES
    (1, 1, 'Galería A, Nivel 2, Nicho 145',  'marmol',  'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_1.jpg'),
    (2, 1, 'Sección 3, Fila 8, Tumba 22',    'granito', 'regular',     'https://storage.cuidadoeterno.cl/espacios/espacio_2.jpg'),
    (3, 2, 'Mausoleo Familiar Bloque M-04',  'cemento', 'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_3.jpg'),
    (4, 3, 'Columbario Norte, Urna 089',      'madera',  'bueno',       'https://storage.cuidadoeterno.cl/espacios/espacio_4.jpg'),
    (5, 4, 'Bóveda Sector B, Lote 17',       'piedra',  'deteriorado', 'https://storage.cuidadoeterno.cl/espacios/espacio_5.jpg');

-- ============================================================
-- 13. FALLECIDO (depende de ESPACIO)
-- ============================================================
INSERT INTO FALLECIDO (id_espacio, rut_fallecido, nombres, apellidos, fecha_nacimiento, fecha_defuncion, epitafio) VALUES
    (1, '98765432-1', 'Manuel Eduardo',   'Soto Ramírez',    '1940-05-12', '2020-11-03', 'Amado esposo y padre, siempre en nuestros corazones'),
    (2, '87654321-0', 'Carmen Rosa',      'Díaz Moreno',     '1935-09-28', '2019-06-15', 'Tu amor nos guía eternamente'),
    (3, '76543210-K', 'Roberto Andrés',   'Vega Castillo',   '1928-01-07', '2021-03-22', 'Descansa en paz, buen hombre'),
    (4, '65432109-9', 'Elena Beatriz',    'Fuentes Alarcón', '1952-12-18', '2022-08-10', NULL),
    (5, '54321098-8', 'Francisco Javier', 'Muñoz Torres',    '1945-04-03', '2023-01-28', 'En el recuerdo vive quien amamos');

-- ============================================================
-- 14. PUESTO_VENTA (depende de CEMENTERIO)
-- ============================================================
INSERT INTO PUESTO_VENTA (id_cementerio, nombre_local, ubicacion_ref, telefono, estado_puesto) VALUES
    (1, 'Florería El Ángel',          'Entrada principal, lado derecho, local 3',    '+56922111333', 1),
    (1, 'Insumos Cementerio Norte',   'Galería B, frente a administración',           '+56933222444', 1),
    (2, 'Flores y Velas Santa Rosa',  'Acceso sur, kiosco 2',                         '+56944333555', 1),
    (3, 'Todo para el Recuerdo',      'Estacionamiento, módulo prefabricado azul',    '+56955444666', 1),
    (4, 'Bazar Porteño Memorias',     'Portería principal, frente al mapa del parque','+56966555777', 1);

-- ============================================================
-- 15. CATALOGO_PRODUCTO (depende de PUESTO_VENTA y PRODUCTO)
-- ============================================================
INSERT INTO CATALOGO_PRODUCTO (id_puesto, id_producto, precio_venta, hay_stock) VALUES
    (1, 2, 12500.00, 1),  -- Florería El Ángel: arreglo floral rosas
    (1, 5,  6200.00, 1),  -- Florería El Ángel: velas aromáticas
    (2, 1,  4800.00, 1),  -- Insumos Norte: escoba industrial
    (2, 4,  3500.00, 1),  -- Insumos Norte: detergente
    (3, 3,  7900.00, 0);  -- Flores Santa Rosa: pintura (sin stock)

-- ============================================================
-- REFERENCIA DE IDs generados
-- ============================================================
--
-- ROL:            1=CLIENTE, 2=CUIDADOR, 3=ADMINISTRADOR
-- REGION:         1=RM, 2=Valparaíso, 3=Biobío, 4=Araucanía, 5=Los Lagos
-- PROVINCIA:      1=Santiago, 2=Cordillera, 3=Valparaíso, 4=Concepción, 5=Cautín
-- COMUNA:         1=Santiago Centro, 2=Recoleta, 3=Puente Alto, 4=Valparaíso, 5=Concepción
-- HORARIO:        1=lunes, 2=martes, 3=miércoles, 4=jueves, 5=viernes
-- TIPO_CUENTA:    1=corriente, 2=vista, 3=ahorro
-- TIPO_ESPACIO:   1=Nicho, 2=Tumba, 3=Mausoleo, 4=Columbario, 5=Bóveda
-- TIPO_SOLICITUD: 1=Limpieza básica, 2=Limpieza profunda, 3=Ofrenda floral,
--                 4=Pintura y restauración, 5=Fotografía de estado
-- TIPO_PAGO:      1=Webpay, 2=transferencia, 3=Khipu, 4=efectivo, 5=oneclick
-- PRODUCTO:       1=Escoba, 2=Arreglo rosas, 3=Pintura, 4=Detergente, 5=Velas
-- CEMENTERIO:     1=Gral Santiago, 2=Católico, 3=Parque Recuerdo,
--                 4=Valparaíso, 5=Concepción
-- ESPACIO:        1-5 (uno por cementerio, tipos distintos)
-- FALLECIDO:      1-5 (uno por espacio)
-- PUESTO_VENTA:   1-5
-- CATALOGO_PRODUCTO: 5 filas (productos en distintos puestos)
--
-- NOTA: CREDENCIAL, PERSONA, CLIENTE, CUIDADOR, ADMINISTRADOR,
--       CUENTA_BANCO, SOLICITUD_SERVICIO, PAGO_SOLICITUD,
--       TRANSACCION_PAGO, DETALLE_ORDEN, REGISTRO_EVIDENCIA,
--       RETIRO_INSUMO, CALIFICACION, PAGO_CUIDADOR
--       → Se poblan mediante la API o pruebas de integración.
-- ============================================================

-- ============================================================
-- ADMINISTRADOR INICIAL — ejecutar UNA SOLA VEZ después de
-- que la API esté corriendo y hayas registrado al menos un
-- cliente para verificar que el hash PBKDF2 funciona.
--
-- Pasos:
--   1. POST /api/v1/auth/registro/cliente con cualquier usuario
--   2. Copiar el hash generado de la tabla CREDENCIAL:
--      SELECT clave_hash FROM CREDENCIAL WHERE nombre_usuario = 'ese_usuario';
--   3. Reemplazar <HASH_PBKDF2_REAL> abajo con ese valor
--   4. Ejecutar estos INSERT manualmente desde DBeaver o mariadb CLI
--
-- INSERT INTO CREDENCIAL (nombre_usuario, clave_hash, estado_cuenta, intentos_fallidos, id_rol)
-- VALUES ('admin.sistema', '<HASH_PBKDF2_REAL>', 'activa', 0, 3);
--
-- INSERT INTO PERSONA (rut, nombre, ap_paterno, ap_materno, email, telefono,
--                      fecha_nacimiento, genero, id_credencial)
-- VALUES ('56789012-3', 'Jorge', 'Administrador', NULL,
--         'admin@cuidadoeterno.cl', '+56956789012', '1980-01-10', 'M',
--         LAST_INSERT_ID());
--
-- INSERT INTO ADMINISTRADOR (id_persona, nivel_acceso, cargo, fecha_ingreso)
-- VALUES (LAST_INSERT_ID(), 'total', 'Administrador General', '2022-01-01');
-- ============================================================

SET FOREIGN_KEY_CHECKS = 1;