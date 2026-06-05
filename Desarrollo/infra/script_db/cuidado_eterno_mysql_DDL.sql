-- ============================================================
-- CUIDADO ETERNO - DDL MySQL/MariaDB
-- Versión final corregida y alineada con todas las entidades JPA
-- ============================================================
USE cuidado_eterno;
 
SET FOREIGN_KEY_CHECKS = 0;
 
-- ------------------------------------------------------------
-- CATÁLOGOS BASE (sin dependencias)
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS rol (
    id_rol      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_rol  VARCHAR(30)     NOT NULL,
    CONSTRAINT rol_pk PRIMARY KEY (id_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS region (
    id_region       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_region   VARCHAR(50)     NOT NULL,
    CONSTRAINT region_pk PRIMARY KEY (id_region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS provincia (
    id_provincia        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_provincia    VARCHAR(50)     NOT NULL,
    id_region           INT UNSIGNED    NOT NULL,
    CONSTRAINT provincia_pk PRIMARY KEY (id_provincia),
    CONSTRAINT provincia_region_fk FOREIGN KEY (id_region)
        REFERENCES region (id_region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS comuna (
    id_comuna       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_comuna   VARCHAR(50)     NOT NULL,
    id_provincia    INT UNSIGNED    NOT NULL,
    CONSTRAINT comuna_pk PRIMARY KEY (id_comuna),
    CONSTRAINT comuna_provincia_fk FOREIGN KEY (id_provincia)
        REFERENCES provincia (id_provincia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS horario (
    id_horario              INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    dia_semana              VARCHAR(12)     NOT NULL,
    hora_inicio             TIME            NOT NULL,
    hora_fin                TIME            NOT NULL,
    estado_disponibilidad   TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT horario_pk PRIMARY KEY (id_horario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS tipo_cuenta (
    id_tipo_cuenta  INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_tipo     VARCHAR(20)     NOT NULL,
    CONSTRAINT tipo_cuenta_pk PRIMARY KEY (id_tipo_cuenta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS tipo_espacio (
    id_tipo_espacio     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_tipo         VARCHAR(30)     NOT NULL,
    descripcion         VARCHAR(255)    NOT NULL,
    nivel_complejidad   INT             NOT NULL,
    CONSTRAINT tipo_espacio_pk PRIMARY KEY (id_tipo_espacio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS tipo_solicitud (
    id_tipo_solicitud       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_servicio         VARCHAR(100)    NOT NULL,
    descripcion             VARCHAR(255)    NOT NULL,
    precio_base             DECIMAL(10,2)   NOT NULL,
    duracion_estimada_min   SMALLINT        NOT NULL,
    requiere_insumos        TINYINT(1)      NOT NULL DEFAULT 0,
    estado_sv               TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT tipo_solicitud_pk PRIMARY KEY (id_tipo_solicitud)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS tipo_pago (
    id_tipo_pago    INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_metodo   VARCHAR(50)     NOT NULL,
    activo          TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT tipo_pago_pk PRIMARY KEY (id_tipo_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS producto (
    id_producto     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100)    NOT NULL,
    descripcion     VARCHAR(255)    NOT NULL,
    categoria       VARCHAR(50)     NOT NULL,
    precio_costo    DECIMAL(10,2)   NOT NULL,
    url_imagen      VARCHAR(255)    NOT NULL,
    estado_activo   TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT producto_pk PRIMARY KEY (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- CREDENCIAL
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS credencial (
    id_credencial       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_usuario      VARCHAR(50)     NOT NULL UNIQUE,
    clave_hash          VARCHAR(255)    NOT NULL,
    estado_cuenta       VARCHAR(20)     NOT NULL DEFAULT 'activa',
    ultimo_inicio       DATETIME        NULL,
    intentos_fallidos   INT             NOT NULL DEFAULT 0,
    id_rol              INT UNSIGNED    NOT NULL,
    CONSTRAINT credencial_pk PRIMARY KEY (id_credencial),
    CONSTRAINT credencial_rol_fk FOREIGN KEY (id_rol)
        REFERENCES rol (id_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- PERSONA y subtipos (herencia JOINED)
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS persona (
    id_persona          INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    rut                 VARCHAR(12)     NOT NULL UNIQUE,
    nombre              VARCHAR(50)     NOT NULL,
    ap_paterno          VARCHAR(50)     NOT NULL,
    ap_materno          VARCHAR(50)     NULL,
    email               VARCHAR(100)    NOT NULL UNIQUE,
    telefono            VARCHAR(15)     NOT NULL,
    fecha_nacimiento    DATE            NOT NULL,
    genero              CHAR(1)         NOT NULL,
    id_credencial       INT UNSIGNED    NOT NULL UNIQUE,
    CONSTRAINT persona_pk PRIMARY KEY (id_persona),
    CONSTRAINT persona_credencial_fk FOREIGN KEY (id_credencial)
        REFERENCES credencial (id_credencial)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS administrador (
    id_persona      INT UNSIGNED    NOT NULL,
    nivel_acceso    VARCHAR(30)     NOT NULL,
    cargo           VARCHAR(50)     NOT NULL,
    fecha_ingreso   DATE            NOT NULL,
    CONSTRAINT administrador_pk PRIMARY KEY (id_persona),
    CONSTRAINT administrador_persona_fk FOREIGN KEY (id_persona)
        REFERENCES persona (id_persona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS cliente (
    id_persona          INT UNSIGNED    NOT NULL,
    fecha_registro      DATE            NOT NULL,
    pref_notificacion   VARCHAR(20)     NOT NULL DEFAULT 'email',
    estado_cliente      VARCHAR(20)     NOT NULL DEFAULT 'activo',
    CONSTRAINT cliente_pk PRIMARY KEY (id_persona),
    CONSTRAINT cliente_persona_fk FOREIGN KEY (id_persona)
        REFERENCES persona (id_persona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS cuidador (
    id_persona              INT UNSIGNED    NOT NULL,
    id_horario              INT UNSIGNED    NOT NULL,
    calificacion_promedio   DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    estado_verificacion     VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    estado_disponibilidad   VARCHAR(20)     NOT NULL DEFAULT 'disponible',
    fecha_ingreso           DATE            NOT NULL,
    CONSTRAINT cuidador_pk PRIMARY KEY (id_persona),
    CONSTRAINT cuidador_persona_fk FOREIGN KEY (id_persona)
        REFERENCES persona (id_persona),
    CONSTRAINT cuidador_horario_fk FOREIGN KEY (id_horario)
        REFERENCES horario (id_horario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- CUENTA BANCO y PAGO CUIDADOR
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS cuenta_banco (
    id_cuenta       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_persona      INT UNSIGNED    NOT NULL,
    id_tipo_cuenta  INT UNSIGNED    NOT NULL,
    banco           VARCHAR(100)    NOT NULL,
    numero_cuenta   VARCHAR(30)     NOT NULL,
    rut_titular     VARCHAR(12)     NOT NULL,
    nombre_titular  VARCHAR(150)    NOT NULL,
    estado_activo   TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT cuenta_banco_pk PRIMARY KEY (id_cuenta),
    CONSTRAINT cuenta_banco_cuidador_fk FOREIGN KEY (id_persona)
        REFERENCES cuidador (id_persona),
    CONSTRAINT cuenta_banco_tipo_fk FOREIGN KEY (id_tipo_cuenta)
        REFERENCES tipo_cuenta (id_tipo_cuenta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS pago_cuidador (
    id_pago_cuidador    INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_cuenta           INT UNSIGNED    NOT NULL,
    id_tipo_pago        INT UNSIGNED    NOT NULL,
    monto               DECIMAL(10,2)   NOT NULL,
    fecha_pago          DATETIME        NOT NULL,
    estado_pago         VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    id_transaccion_ext  VARCHAR(100)    NULL,
    comprobante_url     VARCHAR(500)    NULL,
    CONSTRAINT pago_cuidador_pk PRIMARY KEY (id_pago_cuidador),
    CONSTRAINT pago_cuidador_cuenta_fk FOREIGN KEY (id_cuenta)
        REFERENCES cuenta_banco (id_cuenta),
    CONSTRAINT pago_cuidador_tipo_fk FOREIGN KEY (id_tipo_pago)
        REFERENCES tipo_pago (id_tipo_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- GEOGRAFÍA Y CEMENTERIO
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS cementerio (
    id_cementerio       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_comuna           INT UNSIGNED    NOT NULL,
    id_horario          INT UNSIGNED    NOT NULL,
    nombre_cementerio   VARCHAR(100)    NOT NULL,
    direccion           VARCHAR(255)    NOT NULL,
    latitud             DECIMAL(10,7)   NOT NULL,
    longitud            DECIMAL(10,7)   NOT NULL,
    CONSTRAINT cementerio_pk PRIMARY KEY (id_cementerio),
    CONSTRAINT cementerio_comuna_fk FOREIGN KEY (id_comuna)
        REFERENCES comuna (id_comuna),
    CONSTRAINT cementerio_horario_fk FOREIGN KEY (id_horario)
        REFERENCES horario (id_horario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS espacio (
    id_espacio          INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_tipo_espacio     INT UNSIGNED    NOT NULL,
    id_cementerio       INT UNSIGNED    NOT NULL,
    sector_pabellon     VARCHAR(50)     NOT NULL,
    numero_sepultura    VARCHAR(20)     NOT NULL,
    coordenada_latitud  DECIMAL(10, 8)  NOT NULL,
    coordenada_longitud DECIMAL(11, 8)  NOT NULL,
    material_principal  VARCHAR(50)     NOT NULL,
    estado_fisico       VARCHAR(20)     NOT NULL,
    url_foto_referencia VARCHAR(500)    NULL,
    CONSTRAINT espacio_pk PRIMARY KEY (id_espacio),
    CONSTRAINT espacio_tipo_fk FOREIGN KEY (id_tipo_espacio)
        REFERENCES tipo_espacio (id_tipo_espacio),
    CONSTRAINT espacio_cementerio_fk FOREIGN KEY (id_cementerio)
        REFERENCES cementerio (id_cementerio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS fallecido (
    id_fallecido        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_espacio          INT UNSIGNED    NOT NULL,
    nombres             VARCHAR(100)    NOT NULL,
    apellidos           VARCHAR(100)    NOT NULL,
    fecha_nacimiento    DATE            NOT NULL,
    fecha_defuncion     DATE            NOT NULL,
    epitafio            VARCHAR(255)    NULL,
    CONSTRAINT fallecido_pk PRIMARY KEY (id_fallecido),
    CONSTRAINT fallecido_espacio_fk FOREIGN KEY (id_espacio)
        REFERENCES espacio (id_espacio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- PUESTO DE VENTA Y CATÁLOGO
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS puesto_venta (
    id_puesto       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_cementerio   INT UNSIGNED    NOT NULL,
    nombre_local    VARCHAR(100)    NOT NULL,
    ubicacion_ref   VARCHAR(255)    NOT NULL,
    telefono        VARCHAR(15)     NOT NULL,
    estado_puesto   TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT puesto_venta_pk PRIMARY KEY (id_puesto),
    CONSTRAINT puesto_venta_cementerio_fk FOREIGN KEY (id_cementerio)
        REFERENCES cementerio (id_cementerio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS catalogo_producto (
    id_catalogo     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_puesto       INT UNSIGNED    NOT NULL,
    id_producto     INT UNSIGNED    NOT NULL,
    precio_venta    DECIMAL(10,2)   NOT NULL,
    hay_stock       TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT catalogo_producto_pk PRIMARY KEY (id_catalogo),
    CONSTRAINT catalogo_puesto_fk FOREIGN KEY (id_puesto)
        REFERENCES puesto_venta (id_puesto),
    CONSTRAINT catalogo_producto_fk FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto),
    CONSTRAINT catalogo_unique UNIQUE (id_puesto, id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- PAGOS
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS pago_solicitud (
    id_transaccion          INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_tipo_pago            INT UNSIGNED    NOT NULL,
    monto_total             DECIMAL(10,2)   NOT NULL,
    fecha_pago              DATETIME        NOT NULL,
    estado_pago             VARCHAR(20)     NOT NULL DEFAULT 'iniciado',
    token_transbank         VARCHAR(70)     NULL,
    codigo_autorizacion     VARCHAR(10)     NULL,
    response_code           SMALLINT        NULL,
    tipo_pago_transbank     VARCHAR(5)      NULL,
    cuotas                  INT             NULL,
    ultimos_4_digitos       CHAR(4)         NULL,
    fecha_transaccion       DATETIME        NULL,
    CONSTRAINT pago_solicitud_pk PRIMARY KEY (id_transaccion),
    CONSTRAINT pago_solicitud_tipo_fk FOREIGN KEY (id_tipo_pago)
        REFERENCES tipo_pago (id_tipo_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- TRANSACCION_PAGO — alineada con TransaccionPago.java
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS transaccion_pago (
    id_transaccion_pago         INT UNSIGNED        NOT NULL AUTO_INCREMENT,
    id_transaccion              INT UNSIGNED        NOT NULL UNIQUE,
    token_ws                    VARCHAR(64)         NOT NULL UNIQUE,
    orden_compra                VARCHAR(26)         NOT NULL UNIQUE,
    session_id                  VARCHAR(61)         NOT NULL,
    monto                       DECIMAL(10,2)       NOT NULL,
    url_retorno                 VARCHAR(500)        NOT NULL,
    url_webpay                  VARCHAR(500)        NULL,
    estado_transaccion          VARCHAR(20)         NOT NULL DEFAULT 'iniciada',
    vci                         VARCHAR(6)          NULL,
    response_code               SMALLINT            NULL,
    tipo_pago                   VARCHAR(5)          NULL,
    numero_cuotas               INT                 NULL,
    monto_cuota                 DECIMAL(10,2)       NULL,
    codigo_autorizacion         VARCHAR(6)          NULL,
    ultimos_4_digitos           CHAR(4)             NULL,
    numero_tarjeta              VARCHAR(19)         NULL,
    tipo_tarjeta                VARCHAR(10)         NULL,
    fecha_transaccion_tbk       DATETIME            NULL,
    fecha_contable              DATE                NULL,
    es_anulacion                TINYINT(1)          NOT NULL DEFAULT 0,
    monto_anulacion             DECIMAL(10,2)       NULL,
    fecha_anulacion             DATETIME            NULL,
    token_anulacion             VARCHAR(64)         NULL,
    codigo_accion_anulacion     VARCHAR(6)          NULL,
    codigo_comercio             VARCHAR(12)         NULL,
    codigo_tienda               VARCHAR(12)         NULL,
    ambiente                    VARCHAR(10)         NOT NULL DEFAULT 'produccion',
    ip_cliente                  VARCHAR(45)         NULL,
    user_agent                  VARCHAR(500)        NULL,
    payload_respuesta           JSON                NULL,
    intentos_confirmacion       INT                 NOT NULL DEFAULT 0,
    fecha_creacion              DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion         DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT transaccion_pago_pk PRIMARY KEY (id_transaccion_pago),
    CONSTRAINT transaccion_pago_solicitud_fk FOREIGN KEY (id_transaccion)
        REFERENCES pago_solicitud (id_transaccion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- SOLICITUD SERVICIO — alineada con SolicitudServicio.java
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS solicitud_servicio (
    id_solicitud        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_cliente          INT UNSIGNED    NOT NULL,
    id_tipo_solicitud   INT UNSIGNED    NOT NULL,
    id_transaccion      INT UNSIGNED    NULL,
    fecha_solicitud     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_solicitud    VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    instrucciones       VARCHAR(255)    NULL,
    total_compra        DECIMAL(10,2)   NOT NULL DEFAULT 0.00,
    CONSTRAINT solicitud_servicio_pk PRIMARY KEY (id_solicitud),
    CONSTRAINT solicitud_cliente_fk FOREIGN KEY (id_cliente)
        REFERENCES cliente (id_persona),
    CONSTRAINT solicitud_tipo_fk FOREIGN KEY (id_tipo_solicitud)
        REFERENCES tipo_solicitud (id_tipo_solicitud),
    CONSTRAINT solicitud_pago_fk FOREIGN KEY (id_transaccion)
        REFERENCES pago_solicitud (id_transaccion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- DETALLE ORDEN — alineada con DetalleOrden.java actualizado
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS detalle_orden (
    id_orden                INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_solicitud            INT UNSIGNED    NOT NULL,
    id_persona_cuidador     INT UNSIGNED    NOT NULL,
    id_producto             INT UNSIGNED    NULL,
    id_pago_cuidador        INT UNSIGNED    NULL,
    id_espacio              INT UNSIGNED    NOT NULL,
    fecha_creacion          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_programada        DATETIME        NOT NULL,
    monto_total             DECIMAL(10,2)   NOT NULL,
    estado_orden            VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    observaciones           VARCHAR(500)    NULL,
    cantidad_productos      INT UNSIGNED    NULL,
    precio_unitario         DECIMAL(10,2)   NULL,
    subtotal                DECIMAL(10,2)   NOT NULL,
    CONSTRAINT detalle_orden_pk PRIMARY KEY (id_orden),
    CONSTRAINT detalle_solicitud_fk FOREIGN KEY (id_solicitud)
        REFERENCES solicitud_servicio (id_solicitud),
    CONSTRAINT detalle_cuidador_fk FOREIGN KEY (id_persona_cuidador)
        REFERENCES cuidador (id_persona),
    CONSTRAINT detalle_producto_fk FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto),
    CONSTRAINT detalle_pago_cuidador_fk FOREIGN KEY (id_pago_cuidador)
        REFERENCES pago_cuidador (id_pago_cuidador),
    CONSTRAINT detalle_espacio_fk FOREIGN KEY (id_espacio)
        REFERENCES espacio (id_espacio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- CALIFICACION — FK desde calificacion hacia detalle_orden
-- UNIQUE en id_orden garantiza 1 calificación por orden
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS calificacion (
    id_calificacion     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    puntuacion          INT             NOT NULL,
    comentario          VARCHAR(500)    NULL,
    fecha_calificacion  DATETIME        NOT NULL,
    id_orden            INT UNSIGNED    NOT NULL UNIQUE,
    CONSTRAINT calificacion_pk PRIMARY KEY (id_calificacion),
    CONSTRAINT calificacion_orden_fk FOREIGN KEY (id_orden)
        REFERENCES detalle_orden (id_orden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- REGISTRO EVIDENCIA Y RETIRO INSUMO
-- ------------------------------------------------------------
 
CREATE TABLE IF NOT EXISTS registro_evidencia (
    id_evidencia        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_orden            INT UNSIGNED    NOT NULL,
    url_foto            VARCHAR(500)    NOT NULL,
    fecha_registro      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion_estado  VARCHAR(255)    NULL,
    tipo_momento        VARCHAR(20)     NOT NULL,
    validado            TINYINT(1)      NOT NULL DEFAULT 0,
    CONSTRAINT registro_evidencia_pk PRIMARY KEY (id_evidencia),
    CONSTRAINT evidencia_orden_fk FOREIGN KEY (id_orden)
        REFERENCES detalle_orden (id_orden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE IF NOT EXISTS retiro_insumo (
    id_retiro       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_orden        INT UNSIGNED    NOT NULL,
    id_puesto       INT UNSIGNED    NOT NULL,
    fecha_retiro    DATETIME        NOT NULL,
    monto_total     DECIMAL(10,2)   NOT NULL,
    url_boleta_foto VARCHAR(500)    NULL,
    estado_retiro   VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    CONSTRAINT retiro_insumo_pk PRIMARY KEY (id_retiro),
    CONSTRAINT retiro_orden_fk FOREIGN KEY (id_orden)
        REFERENCES detalle_orden (id_orden),
    CONSTRAINT retiro_puesto_fk FOREIGN KEY (id_puesto)
        REFERENCES puesto_venta (id_puesto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
SET FOREIGN_KEY_CHECKS = 1;