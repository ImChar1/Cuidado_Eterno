-- ============================================================
-- CUIDADO ETERNO - DDL MySQL/MariaDB
-- Corregido desde Oracle SQL Developer Data Modeler
-- ============================================================
 
SET FOREIGN_KEY_CHECKS = 0;
 
-- ------------------------------------------------------------
-- CATÁLOGOS BASE (sin dependencias)
-- ------------------------------------------------------------
 
CREATE TABLE ROL (
    id_rol      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_rol  VARCHAR(30)     NOT NULL,
    CONSTRAINT ROL_PK PRIMARY KEY (id_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE REGION (
    id_region       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_region   VARCHAR(50)     NOT NULL,
    CONSTRAINT REGION_PK PRIMARY KEY (id_region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE PROVINCIA (
    id_provincia        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_provincia    VARCHAR(50)     NOT NULL,
    id_region           INT UNSIGNED    NOT NULL,
    CONSTRAINT PROVINCIA_PK PRIMARY KEY (id_provincia),
    CONSTRAINT PROVINCIA_REGION_FK FOREIGN KEY (id_region)
        REFERENCES REGION (id_region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE COMUNA (
    id_comuna       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_comuna   VARCHAR(50)     NOT NULL,
    id_provincia    INT UNSIGNED    NOT NULL,
    CONSTRAINT COMUNA_PK PRIMARY KEY (id_comuna),
    CONSTRAINT COMUNA_PROVINCIA_FK FOREIGN KEY (id_provincia)
        REFERENCES PROVINCIA (id_provincia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE HORARIO (
    id_horario              INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    dia_semana              VARCHAR(12)     NOT NULL,
    hora_inicio             TIME            NOT NULL,
    hora_fin                TIME            NOT NULL,
    estado_disponibilidad   TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT HORARIO_PK PRIMARY KEY (id_horario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE TIPO_CUENTA (
    id_tipo_cuenta  INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_tipo     VARCHAR(20)     NOT NULL,
    CONSTRAINT TIPO_CUENTA_PK PRIMARY KEY (id_tipo_cuenta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE TIPO_ESPACIO (
    id_tipo_espacio     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_tipo         VARCHAR(30)     NOT NULL,
    descripcion         VARCHAR(255)    NOT NULL,
    nivel_complejidad   TINYINT UNSIGNED NOT NULL,
    CONSTRAINT TIPO_ESPACIO_PK PRIMARY KEY (id_tipo_espacio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE TIPO_SOLICITUD (
    id_tipo_solic           INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_servicio         VARCHAR(100)    NOT NULL,
    descripcion             VARCHAR(255)    NOT NULL,
    precio_base             DECIMAL(10,2)   NOT NULL,
    duracion_estimada_min   SMALLINT UNSIGNED NOT NULL,
    requiere_insumos        TINYINT(1)      NOT NULL DEFAULT 0,
    estado_sv               TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT TIPO_SOLICITUD_PK PRIMARY KEY (id_tipo_solic)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE TIPO_PAGO (
    id_tipo_pago    INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_metodo   VARCHAR(50)     NOT NULL,
    activo          TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT TIPO_PAGO_PK PRIMARY KEY (id_tipo_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE CALIFICACION (
    id_calificacion     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    puntuacion          TINYINT UNSIGNED NOT NULL,
    comentario          VARCHAR(255)    NOT NULL,
    fecha_evaluacion    DATETIME        NOT NULL,
    CONSTRAINT CALIFICACION_PK PRIMARY KEY (id_calificacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE PRODUCTO (
    id_producto     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100)    NOT NULL,
    descripcion     VARCHAR(255)    NOT NULL,
    categoria       VARCHAR(50)     NOT NULL,
    precio_costo    DECIMAL(10,2)   NOT NULL,
    url_imagen      VARCHAR(255)    NOT NULL,
    estado_activo   TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT PRODUCTO_PK PRIMARY KEY (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- CREDENCIAL
-- ------------------------------------------------------------
 
CREATE TABLE CREDENCIAL (
    id_credencial       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_usuario      VARCHAR(50)     NOT NULL UNIQUE,
    clave_hash          VARCHAR(255)    NOT NULL,
    estado_cuenta       VARCHAR(20)     NOT NULL DEFAULT 'activa',
    ultimo_inicio       DATETIME        NULL,
    intentos_fallidos   TINYINT UNSIGNED NOT NULL DEFAULT 0,
    id_rol              INT UNSIGNED    NOT NULL,
    CONSTRAINT CREDENCIAL_PK PRIMARY KEY (id_credencial),
    CONSTRAINT CREDENCIAL_ROL_FK FOREIGN KEY (id_rol)
        REFERENCES ROL (id_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- PERSONA y subtipos
-- ------------------------------------------------------------
 
CREATE TABLE PERSONA (
    id_persona      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    rut             VARCHAR(12)     NOT NULL UNIQUE,
    nombre          VARCHAR(50)     NOT NULL,
    ap_paterno      VARCHAR(50)     NOT NULL,
    ap_materno      VARCHAR(50)     NULL,
    email           VARCHAR(100)    NOT NULL UNIQUE,
    telefono        VARCHAR(15)     NOT NULL,
    fecha_nacimiento DATE           NOT NULL,
    genero          CHAR(1)         NOT NULL,
    id_credencial   INT UNSIGNED    NOT NULL UNIQUE,
    CONSTRAINT PERSONA_PK PRIMARY KEY (id_persona),
    CONSTRAINT PERSONA_CREDENCIAL_FK FOREIGN KEY (id_credencial)
        REFERENCES CREDENCIAL (id_credencial)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE ADMINISTRADOR (
    id_persona      INT UNSIGNED    NOT NULL,
    nivel_acceso    VARCHAR(30)     NOT NULL,
    cargo           VARCHAR(50)     NOT NULL,
    fecha_ingreso   DATE            NOT NULL,
    CONSTRAINT ADMINISTRADOR_PK PRIMARY KEY (id_persona),
    CONSTRAINT ADMINISTRADOR_PERSONA_FK FOREIGN KEY (id_persona)
        REFERENCES PERSONA (id_persona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE CLIENTE (
    id_persona          INT UNSIGNED    NOT NULL,
    fecha_registro      DATE            NOT NULL,
    pref_notificacion   VARCHAR(20)     NOT NULL DEFAULT 'email',
    estado_cliente      VARCHAR(20)     NOT NULL DEFAULT 'activo',
    CONSTRAINT CLIENTE_PK PRIMARY KEY (id_persona),
    CONSTRAINT CLIENTE_PERSONA_FK FOREIGN KEY (id_persona)
        REFERENCES PERSONA (id_persona)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE CUIDADOR (
    id_persona              INT UNSIGNED    NOT NULL,
    id_horario              INT UNSIGNED    NOT NULL,
    calificacion_promedio   DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    estado_verificacion     VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    estado_disponibilidad   VARCHAR(20)     NOT NULL DEFAULT 'disponible',
    fecha_ingreso           DATE            NOT NULL,
    CONSTRAINT CUIDADOR_PK PRIMARY KEY (id_persona),
    CONSTRAINT CUIDADOR_PERSONA_FK FOREIGN KEY (id_persona)
        REFERENCES PERSONA (id_persona),
    CONSTRAINT CUIDADOR_HORARIO_FK FOREIGN KEY (id_horario)
        REFERENCES HORARIO (id_horario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- CUENTA BANCO y PAGO CUIDADOR
-- ------------------------------------------------------------
 
CREATE TABLE CUENTA_BANCO (
    id_cuenta       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_persona      INT UNSIGNED    NOT NULL,
    id_tipo_cuenta  INT UNSIGNED    NOT NULL,
    banco           VARCHAR(100)    NOT NULL,
    numero_cuenta   VARCHAR(30)     NOT NULL,
    rut_titular     VARCHAR(12)     NOT NULL,
    nombre_titular  VARCHAR(150)    NOT NULL,
    estado_activo   TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT CUENTA_BANCO_PK PRIMARY KEY (id_cuenta),
    CONSTRAINT CUENTA_BANCO_CUIDADOR_FK FOREIGN KEY (id_persona)
        REFERENCES CUIDADOR (id_persona),
    CONSTRAINT CUENTA_BANCO_TIPO_FK FOREIGN KEY (id_tipo_cuenta)
        REFERENCES TIPO_CUENTA (id_tipo_cuenta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE PAGO_CUIDADOR (
    id_pago_cuidador    INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_cuenta           INT UNSIGNED    NOT NULL,
    id_tipo_pago        INT UNSIGNED    NOT NULL,
    monto               DECIMAL(10,2)   NOT NULL,
    fecha_pago          DATETIME        NOT NULL,
    estado_pago         VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    id_transaccion_ext  VARCHAR(100)    NULL,
    comprobante_url     VARCHAR(500)    NULL,
    CONSTRAINT PAGO_CUIDADOR_PK PRIMARY KEY (id_pago_cuidador),
    CONSTRAINT PAGO_CUIDADOR_CUENTA_FK FOREIGN KEY (id_cuenta)
        REFERENCES CUENTA_BANCO (id_cuenta),
    CONSTRAINT PAGO_CUIDADOR_TIPO_FK FOREIGN KEY (id_tipo_pago)
        REFERENCES TIPO_PAGO (id_tipo_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- GEOGRAFÍA Y CEMENTERIO
-- ------------------------------------------------------------
 
CREATE TABLE CEMENTERIO (
    id_cementerio       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_comuna           INT UNSIGNED    NOT NULL,
    id_horario          INT UNSIGNED    NOT NULL,
    nombre_cementerio   VARCHAR(100)    NOT NULL,
    direccion           VARCHAR(255)    NOT NULL,
    latitud             DECIMAL(10,7)   NOT NULL,
    longitud            DECIMAL(10,7)   NOT NULL,
    CONSTRAINT CEMENTERIO_PK PRIMARY KEY (id_cementerio),
    CONSTRAINT CEMENTERIO_COMUNA_FK FOREIGN KEY (id_comuna)
        REFERENCES COMUNA (id_comuna),
    CONSTRAINT CEMENTERIO_HORARIO_FK FOREIGN KEY (id_horario)
        REFERENCES HORARIO (id_horario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE ESPACIO (
    id_espacio          INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_tipo_espacio     INT UNSIGNED    NOT NULL,
    id_cementerio       INT UNSIGNED    NOT NULL,
    ubicacion_interna   VARCHAR(150)    NOT NULL,
    material_principal  VARCHAR(50)     NOT NULL,
    estado_fisico       VARCHAR(20)     NOT NULL,
    url_foto_referencia VARCHAR(500)    NULL,
    CONSTRAINT ESPACIO_PK PRIMARY KEY (id_espacio),
    CONSTRAINT ESPACIO_TIPO_FK FOREIGN KEY (id_tipo_espacio)
        REFERENCES TIPO_ESPACIO (id_tipo_espacio),
    CONSTRAINT ESPACIO_CEMENTERIO_FK FOREIGN KEY (id_cementerio)
        REFERENCES CEMENTERIO (id_cementerio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE FALLECIDO (
    id_fallecido    INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_espacio      INT UNSIGNED    NULL,
    rut_fallecido   VARCHAR(12)     NOT NULL,
    nombres         VARCHAR(100)    NOT NULL,
    apellidos       VARCHAR(100)    NOT NULL,
    fecha_nacimiento DATE           NOT NULL,
    fecha_defuncion  DATE           NOT NULL,
    epitafio        VARCHAR(255)    NULL,
    CONSTRAINT FALLECIDO_PK PRIMARY KEY (id_fallecido),
    CONSTRAINT FALLECIDO_ESPACIO_FK FOREIGN KEY (id_espacio)
        REFERENCES ESPACIO (id_espacio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- PUESTO DE VENTA Y CATÁLOGO
-- ------------------------------------------------------------
 
CREATE TABLE PUESTO_VENTA (
    id_puesto       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_cementerio   INT UNSIGNED    NOT NULL,
    nombre_local    VARCHAR(100)    NOT NULL,
    ubicacion_ref   VARCHAR(255)    NOT NULL,
    telefono        VARCHAR(15)     NOT NULL,
    estado_puesto   TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT PUESTO_VENTA_PK PRIMARY KEY (id_puesto),
    CONSTRAINT PUESTO_VENTA_CEMENTERIO_FK FOREIGN KEY (id_cementerio)
        REFERENCES CEMENTERIO (id_cementerio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE CATALOGO_PRODUCTO (
    id_catalogo     INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_puesto       INT UNSIGNED    NOT NULL,
    id_producto     INT UNSIGNED    NOT NULL,
    precio_venta    DECIMAL(10,2)   NOT NULL,
    hay_stock       TINYINT(1)      NOT NULL DEFAULT 1,
    CONSTRAINT CATALOGO_PRODUCTO_PK PRIMARY KEY (id_catalogo),
    CONSTRAINT CATALOGO_PUESTO_FK FOREIGN KEY (id_puesto)
        REFERENCES PUESTO_VENTA (id_puesto),
    CONSTRAINT CATALOGO_PRODUCTO_FK FOREIGN KEY (id_producto)
        REFERENCES PRODUCTO (id_producto),
    CONSTRAINT CATALOGO_UNIQUE UNIQUE (id_puesto, id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- SOLICITUD Y PAGOS
-- ------------------------------------------------------------
 
CREATE TABLE PAGO_SOLICITUD (
    id_transaccion      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_tipo_pago        INT UNSIGNED    NOT NULL,
    monto_total         DECIMAL(10,2)   NOT NULL,
    fecha_pago          DATETIME        NOT NULL,
    estado_pago         VARCHAR(20)     NOT NULL DEFAULT 'iniciado',
    token_transbank     VARCHAR(70)     NULL,
    codigo_autorizacion VARCHAR(10)     NULL,
    response_code       SMALLINT        NULL,
    tipo_pago_transbank VARCHAR(5)      NULL,
    cuotas              TINYINT UNSIGNED NULL,
    ultimos_4_digitos   CHAR(4)         NULL,
    fecha_transaccion   DATETIME        NULL,
    CONSTRAINT PAGO_SOLICITUD_PK PRIMARY KEY (id_transaccion),
    CONSTRAINT PAGO_SOLICITUD_TIPO_FK FOREIGN KEY (id_tipo_pago)
        REFERENCES TIPO_PAGO (id_tipo_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE SOLICITUD_SERVICIO (
    id_solicitud        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_persona          INT UNSIGNED    NOT NULL,
    id_tipo_solic       INT UNSIGNED    NOT NULL,
    id_transaccion      INT UNSIGNED    NULL,
    fecha_creacion      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_solicitud    VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    instrucciones       VARCHAR(255)    NULL,
    total_compra        DECIMAL(10,2)   NOT NULL DEFAULT 0.00,
    CONSTRAINT SOLICITUD_SERVICIO_PK PRIMARY KEY (id_solicitud),
    CONSTRAINT SOLICITUD_CLIENTE_FK FOREIGN KEY (id_persona)
        REFERENCES CLIENTE (id_persona),
    CONSTRAINT SOLICITUD_TIPO_FK FOREIGN KEY (id_tipo_solic)
        REFERENCES TIPO_SOLICITUD (id_tipo_solic),
    CONSTRAINT SOLICITUD_PAGO_FK FOREIGN KEY (id_transaccion)
        REFERENCES PAGO_SOLICITUD (id_transaccion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
-- ------------------------------------------------------------
-- DETALLE ORDEN Y TABLAS DEPENDIENTES
-- ------------------------------------------------------------
 
CREATE TABLE DETALLE_ORDEN (
    id_orden                INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_solicitud            INT UNSIGNED    NOT NULL,
    id_persona_cuidador     INT UNSIGNED    NOT NULL,
    id_producto             INT UNSIGNED    NOT NULL,
    id_calificacion         INT UNSIGNED    NULL,
    id_pago_cuidador        INT UNSIGNED    NULL,
    id_espacio              INT UNSIGNED    NULL,
    cantidad_productos      INT UNSIGNED    NOT NULL,
    precio_unitario         DECIMAL(10,2)   NOT NULL,
    subtotal                DECIMAL(10,2)   NOT NULL,
    estado_orden            VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    CONSTRAINT DETALLE_ORDEN_PK PRIMARY KEY (id_orden),
    CONSTRAINT DETALLE_SOLICITUD_FK FOREIGN KEY (id_solicitud)
        REFERENCES SOLICITUD_SERVICIO (id_solicitud),
    CONSTRAINT DETALLE_CUIDADOR_FK FOREIGN KEY (id_persona_cuidador)
        REFERENCES CUIDADOR (id_persona),
    CONSTRAINT DETALLE_PRODUCTO_FK FOREIGN KEY (id_producto)
        REFERENCES PRODUCTO (id_producto),
    CONSTRAINT DETALLE_CALIFICACION_FK FOREIGN KEY (id_calificacion)
        REFERENCES CALIFICACION (id_calificacion),
    CONSTRAINT DETALLE_PAGO_CUIDADOR_FK FOREIGN KEY (id_pago_cuidador)
        REFERENCES PAGO_CUIDADOR (id_pago_cuidador),
    CONSTRAINT DETALLE_ESPACIO_FK FOREIGN KEY (id_espacio)
        REFERENCES ESPACIO (id_espacio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE REGISTRO_EVIDENCIA (
    id_evidencia        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_orden            INT UNSIGNED    NOT NULL,
    url_foto            VARCHAR(500)    NOT NULL,
    fecha_registro      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion_estado  VARCHAR(255)    NULL,
    tipo_momento        VARCHAR(20)     NOT NULL,
    validado            TINYINT(1)      NOT NULL DEFAULT 0,
    CONSTRAINT REGISTRO_EVIDENCIA_PK PRIMARY KEY (id_evidencia),
    CONSTRAINT EVIDENCIA_ORDEN_FK FOREIGN KEY (id_orden)
        REFERENCES DETALLE_ORDEN (id_orden)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
CREATE TABLE RETIRO_INSUMO (
    id_retiro       INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    id_orden        INT UNSIGNED    NOT NULL,
    id_puesto       INT UNSIGNED    NOT NULL,
    fecha_retiro    DATETIME        NOT NULL,
    monto_total     DECIMAL(10,2)   NOT NULL,
    url_boleta_foto VARCHAR(500)    NULL,
    estado_retiro   VARCHAR(20)     NOT NULL DEFAULT 'pendiente',
    CONSTRAINT RETIRO_INSUMO_PK PRIMARY KEY (id_retiro),
    CONSTRAINT RETIRO_ORDEN_FK FOREIGN KEY (id_orden)
        REFERENCES DETALLE_ORDEN (id_orden),
    CONSTRAINT RETIRO_PUESTO_FK FOREIGN KEY (id_puesto)
        REFERENCES PUESTO_VENTA (id_puesto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 
SET FOREIGN_KEY_CHECKS = 1;