-- Generado por Oracle SQL Developer Data Modeler 24.3.1.351.0831
--   en:        2026-05-11 10:20:32 CLT
--   sitio:      Oracle Database 11g
--   tipo:      Oracle Database 11g



-- predefined type, no DDL - MDSYS.SDO_GEOMETRY

-- predefined type, no DDL - XMLTYPE

CREATE TABLE ADMINISTRADOR 
    ( 
     id_persona               INTEGER  NOT NULL , 
     id_admin                 INTEGER  NOT NULL , 
     nivel_acceso             VARCHAR2 (12)  NOT NULL , 
     cargo                    VARCHAR2 (50)  NOT NULL , 
     fecha_ingreso            DATE  NOT NULL , 
     CEMENTERIO_id_region     INTEGER  NOT NULL , 
     CEMENTERIO_id_provincia  INTEGER  NOT NULL , 
     CEMENTERIO_id_cementerio INTEGER  NOT NULL 
    ) 
;

ALTER TABLE ADMINISTRADOR 
    ADD CONSTRAINT ADMINISTRADOR_PK PRIMARY KEY ( id_persona ) ;

ALTER TABLE ADMINISTRADOR 
    ADD CONSTRAINT ADMINISTRADOR_PKv1 UNIQUE ( id_admin ) ;

CREATE TABLE CALIFICACION 
    ( 
     id_calificacion  INTEGER  NOT NULL , 
     puntuacion       INTEGER  NOT NULL , 
     comentario       VARCHAR2 (255)  NOT NULL , 
     fecha_evaluacion DATE  NOT NULL 
    ) 
;

ALTER TABLE CALIFICACION 
    ADD CONSTRAINT CALIFICACION_PK PRIMARY KEY ( id_calificacion ) ;

CREATE TABLE CATALOGO_PRODUCTO 
    ( 
     id_catalogo            INTEGER  NOT NULL , 
     hay_stock              SMALLINT  NOT NULL , 
     precio_costo           NUMBER (10,2)  NOT NULL , 
     PRODUCTO_id_producto   INTEGER  NOT NULL , 
     PUESTO_VENTA_id_puesto INTEGER  NOT NULL 
    ) 
;

ALTER TABLE CATALOGO_PRODUCTO 
    ADD CONSTRAINT CATALOGO_PRODUCTO_PK PRIMARY KEY ( id_catalogo ) ;

CREATE TABLE CEMENTERIO 
    ( 
     id_region          INTEGER  NOT NULL , 
     id_provincia       INTEGER  NOT NULL , 
     id_cementerio      INTEGER  NOT NULL , 
     nombre_cementerio  VARCHAR2 (50)  NOT NULL , 
     direccion          VARCHAR2 (255)  NOT NULL , 
     latitud            NUMBER  NOT NULL , 
     longitud           NUMBER  NOT NULL , 
     HORARIO_id_horario INTEGER  NOT NULL , 
     COMUNA_id_comuna   INTEGER  NOT NULL 
    ) 
;

ALTER TABLE CEMENTERIO 
    ADD CONSTRAINT CEMENTERIO_PK PRIMARY KEY ( id_region, id_provincia, id_cementerio ) ;

CREATE TABLE CLIENTE 
    ( 
     id_persona        INTEGER  NOT NULL , 
     id_cliente        INTEGER  NOT NULL , 
     fecha_registro    DATE  NOT NULL , 
     pref_notificacion VARCHAR2 (12)  NOT NULL , 
     estado_cliente    VARCHAR2 (12)  NOT NULL 
    ) 
;

ALTER TABLE CLIENTE 
    ADD CONSTRAINT CLIENTE_PK PRIMARY KEY ( id_persona ) ;

ALTER TABLE CLIENTE 
    ADD CONSTRAINT CLIENTE_PKv1 UNIQUE ( id_cliente ) ;

CREATE TABLE COMUNA 
    ( 
     id_comuna              INTEGER  NOT NULL , 
     nombre_comuna          VARCHAR2 (30)  NOT NULL , 
     PROVINCIA_id_provincia INTEGER  NOT NULL 
    ) 
;

ALTER TABLE COMUNA 
    ADD CONSTRAINT COMUNA_PK PRIMARY KEY ( id_comuna ) ;

CREATE TABLE CREDENCIAL 
    ( 
     id_credencial     INTEGER  NOT NULL , 
     nombre_usuario    VARCHAR2 (50)  NOT NULL , 
     clave_hash        VARCHAR2 (255)  NOT NULL , 
     estado_cuenta     VARCHAR2 (12)  NOT NULL , 
     ultimo_inicio     DATE  NOT NULL , 
     intentos_fallidos INTEGER  NOT NULL , 
     ROL_id_rol        INTEGER  NOT NULL 
    ) 
;

ALTER TABLE CREDENCIAL 
    ADD CONSTRAINT CREDENCIAL_PK PRIMARY KEY ( id_credencial ) ;

CREATE TABLE CUENTA_BANCO 
    ( 
     id_cuenta                  INTEGER  NOT NULL , 
     banco                      VARCHAR2 (100)  NOT NULL , 
     numero_cuenta              VARCHAR2 (30)  NOT NULL , 
     rut_titular                VARCHAR2 (12)  NOT NULL , 
     nombre_titular             VARCHAR2 (150)  NOT NULL , 
     estado_cuenta              SMALLINT  NOT NULL , 
     CUIDADOR_id_cuidador       INTEGER  NOT NULL , 
     TIPO_CUENTA_id_tipo_cuenta INTEGER  NOT NULL 
    ) 
;
CREATE UNIQUE INDEX CUENTA_BANCO__IDX ON CUENTA_BANCO 
    ( 
     CUIDADOR_id_cuidador ASC 
    ) 
;

ALTER TABLE CUENTA_BANCO 
    ADD CONSTRAINT CUENTA_BANCO_PK PRIMARY KEY ( id_cuenta ) ;

CREATE TABLE CUIDADOR 
    ( 
     id_persona            INTEGER  NOT NULL , 
     id_cuidador           INTEGER  NOT NULL , 
     calificacion_promedio NUMBER  NOT NULL , 
     estado_verificacion   VARCHAR2 (12)  NOT NULL , 
     estado_disponibilidad VARCHAR2 (12)  NOT NULL , 
     fecha_ingreso         DATE  NOT NULL , 
     HORARIO_id_horario    INTEGER  NOT NULL 
    ) 
;

ALTER TABLE CUIDADOR 
    ADD CONSTRAINT CUIDADOR_PK PRIMARY KEY ( id_persona ) ;

ALTER TABLE CUIDADOR 
    ADD CONSTRAINT CUIDADOR_PKv1 UNIQUE ( id_cuidador ) ;

CREATE TABLE DETALLE_ORDEN 
    ( 
     cantidad_productos             INTEGER  NOT NULL , 
     id_orden                       INTEGER  NOT NULL , 
     precio_unitario                NUMBER  NOT NULL , 
     subtotal                       NUMBER  NOT NULL , 
     estado_orden                   VARCHAR2 (20)  NOT NULL , 
     PAGO_CUIDADOR_id_pago_cuidador INTEGER  NOT NULL , 
     CUIDADOR_id_cuidador           INTEGER  NOT NULL , 
     PRODUCTO_id_producto           INTEGER  NOT NULL , 
     CALIFICACION_id_calificacion   INTEGER  NOT NULL 
    ) 
;
CREATE UNIQUE INDEX DETALLE_ORDEN__IDX ON DETALLE_ORDEN 
    ( 
     PAGO_CUIDADOR_id_pago_cuidador ASC 
    ) 
;

ALTER TABLE DETALLE_ORDEN 
    ADD CONSTRAINT DETALLE_ORDEN_PK PRIMARY KEY ( id_orden ) ;

CREATE TABLE ESPACIO 
    ( 
     id_espacio                   INTEGER  NOT NULL , 
     ubicacion_interna            VARCHAR2 (150)  NOT NULL , 
     material_principal           VARCHAR2 (12)  NOT NULL , 
     estado_fisico                VARCHAR2 (12)  NOT NULL , 
     url_foto_referencia          VARCHAR2 (255)  NOT NULL , 
     DETALLE_ORDEN_id_orden       INTEGER  NOT NULL , 
     TIPO_ESPACIO_id_tipo_espacio INTEGER  NOT NULL , 
     CEMENTERIO_id_region         INTEGER  NOT NULL , 
     CEMENTERIO_id_provincia      INTEGER  NOT NULL , 
     CEMENTERIO_id_cementerio     INTEGER  NOT NULL 
    ) 
;
CREATE UNIQUE INDEX ESPACIO__IDX ON ESPACIO 
    ( 
     DETALLE_ORDEN_id_orden ASC 
    ) 
;

ALTER TABLE ESPACIO 
    ADD CONSTRAINT ESPACIO_PK PRIMARY KEY ( id_espacio ) ;

CREATE TABLE FALLECIDO 
    ( 
     id_fallecido       INTEGER  NOT NULL , 
     rut_fallecido      VARCHAR2 (12)  NOT NULL , 
     nombres            VARCHAR2 (100)  NOT NULL , 
     apellidos          VARCHAR2 (100)  NOT NULL , 
     fecha_nacimiento   DATE  NOT NULL , 
     fecha_defuncion    DATE  NOT NULL , 
     eipitafio          VARCHAR2 (255)  NOT NULL , 
     ESPACIO_id_espacio INTEGER 
    ) 
;
CREATE UNIQUE INDEX FALLECIDO__IDX ON FALLECIDO 
    ( 
     ESPACIO_id_espacio ASC 
    ) 
;

ALTER TABLE FALLECIDO 
    ADD CONSTRAINT FALLECIDO_PK PRIMARY KEY ( id_fallecido ) ;

CREATE TABLE HORARIO 
    ( 
     id_horario            INTEGER  NOT NULL , 
     dia_semana            VARCHAR2 (12)  NOT NULL , 
     hora_inicio           DATE  NOT NULL , 
     hora_fin              DATE  NOT NULL , 
     estado_disponibilidad CHAR (1)  NOT NULL 
    ) 
;

ALTER TABLE HORARIO 
    ADD CONSTRAINT HORARIO_PK PRIMARY KEY ( id_horario ) ;

CREATE TABLE PAGO_CUIDADOR 
    ( 
     id_pago_cuidador       INTEGER  NOT NULL , 
     metodo_pago            VARCHAR2 (30)  NOT NULL , 
     estado_pago            VARCHAR2 (20)  NOT NULL , 
     comprobante_url        VARCHAR2 (500)  NOT NULL , 
     CUENTA_BANCO_id_cuenta INTEGER  NOT NULL 
    ) 
;

ALTER TABLE PAGO_CUIDADOR 
    ADD CONSTRAINT PAGO_CUIDADOR_PK PRIMARY KEY ( id_pago_cuidador ) ;

CREATE TABLE PAGO_SOLICITUD 
    ( 
     id_transaccion      INTEGER  NOT NULL , 
     monto_total         NUMBER (10,2)  NOT NULL , 
     fecha_pago          DATE  NOT NULL , 
     estado_pago         VARCHAR2 (20)  NOT NULL , 
     id_transacion       VARCHAR2 (100)  NOT NULL , 
     token_transbank     VARCHAR2 (70)  NOT NULL , 
     codigo_autorizacion VARCHAR2 (10)  NOT NULL , 
     response_code       SMALLINT  NOT NULL , 
     tipo_pago_transbank VARCHAR2 (5)  NOT NULL , 
     cuotas              SMALLINT  NOT NULL , 
     ultimos_4_digitos   CHAR (4)  NOT NULL , 
     fecha_transaccion   DATE  NOT NULL 
    ) 
;

ALTER TABLE PAGO_SOLICITUD 
    ADD CONSTRAINT PAGO_SOLICITUD_PK PRIMARY KEY ( id_transaccion ) ;

CREATE TABLE PERSONA 
    ( 
     id_persona               INTEGER  NOT NULL , 
     rut                      VARCHAR2 (12)  NOT NULL , 
     nombre                   VARCHAR2 (50)  NOT NULL , 
     ap_paterno               VARCHAR2 (50)  NOT NULL , 
     ap_materno               VARCHAR2 (50) , 
     email                    VARCHAR2 (100)  NOT NULL , 
     telefono                 VARCHAR2 (15)  NOT NULL , 
     fecha_nacimiento         DATE  NOT NULL , 
     genero                   CHAR (2)  NOT NULL , 
     tipo_persona             VARCHAR2 (13)  NOT NULL , 
     CREDENCIAL_id_credencial INTEGER  NOT NULL 
    ) 
;

ALTER TABLE PERSONA 
    ADD CONSTRAINT CH_INH_PERSONA 
    CHECK (tipo_persona IN ('ADMINISTRADOR', 'CLIENTE', 'CUIDADOR', 'PERSONA')) 
;

ALTER TABLE PERSONA 
    ADD CONSTRAINT PERSONA_PK PRIMARY KEY ( id_persona ) ;

CREATE TABLE PRODUCTO 
    ( 
     id_producto     INTEGER  NOT NULL , 
     nombre          VARCHAR2 (100)  NOT NULL , 
     descripcion     VARCHAR2 (255)  NOT NULL , 
     categoría       VARCHAR2 (12)  NOT NULL , 
     precio_estimado NUMBER  NOT NULL , 
     url_imagen      VARCHAR2 (255)  NOT NULL , 
     estado_activo   CHAR (1)  NOT NULL 
    ) 
;

ALTER TABLE PRODUCTO 
    ADD CONSTRAINT PRODUCTO_PK PRIMARY KEY ( id_producto ) ;

CREATE TABLE PROVINCIA 
    ( 
     id_provincia     INTEGER  NOT NULL , 
     nombre_provincia VARCHAR2 (20)  NOT NULL , 
     REGION_id_region INTEGER  NOT NULL 
    ) 
;

ALTER TABLE PROVINCIA 
    ADD CONSTRAINT PROVINCIA_PK PRIMARY KEY ( id_provincia ) ;

CREATE TABLE PUESTO_VENTA 
    ( 
     id_puesto                INTEGER  NOT NULL , 
     nombre_local             VARCHAR2 (100)  NOT NULL , 
     ubicacion_ref            VARCHAR2 (255)  NOT NULL , 
     telefono                 VARCHAR2 (15)  NOT NULL , 
     estado_puesto            SMALLINT  NOT NULL , 
     CEMENTERIO_id_region     INTEGER  NOT NULL , 
     CEMENTERIO_id_provincia  INTEGER  NOT NULL , 
     CEMENTERIO_id_cementerio INTEGER  NOT NULL 
    ) 
;

ALTER TABLE PUESTO_VENTA 
    ADD CONSTRAINT PUESTO_VENTA_PK PRIMARY KEY ( id_puesto ) ;

CREATE TABLE REGION 
    ( 
     id_region     INTEGER  NOT NULL , 
     nombre_region VARCHAR2 (50)  NOT NULL 
    ) 
;

ALTER TABLE REGION 
    ADD CONSTRAINT REGION_PK PRIMARY KEY ( id_region ) ;

CREATE TABLE REGISTRO_EVIDENCIA 
    ( 
     id_evidencia           INTEGER  NOT NULL , 
     url_foto               VARCHAR2 (500)  NOT NULL , 
     fecha_registro         DATE  NOT NULL , 
     descripcion_estado     VARCHAR2 (255)  NOT NULL , 
     tipo_momento           VARCHAR2 (20)  NOT NULL , 
     validado               CHAR (1)  NOT NULL , 
     DETALLE_ORDEN_id_orden INTEGER  NOT NULL 
    ) 
;

ALTER TABLE REGISTRO_EVIDENCIA 
    ADD CONSTRAINT REGISTRO_EVIDENCIA_PK PRIMARY KEY ( id_evidencia ) ;

CREATE TABLE RETIRO_INSUMO 
    ( 
     id_retiro              INTEGER  NOT NULL , 
     fecha_retiro           DATE  NOT NULL , 
     monto_total            NUMBER (10,2)  NOT NULL , 
     url_boleta_foto        VARCHAR2 (500)  NOT NULL , 
     DETALLE_ORDEN_id_orden INTEGER  NOT NULL , 
     estado_retiro          VARCHAR2 (20)  NOT NULL 
    ) 
;

ALTER TABLE RETIRO_INSUMO 
    ADD CONSTRAINT RETIRO_INSUMO_PK PRIMARY KEY ( id_retiro ) ;

CREATE TABLE ROL 
    ( 
     id_rol     INTEGER  NOT NULL , 
     nombre_rol VARCHAR2 (12)  NOT NULL 
    ) 
;

ALTER TABLE ROL 
    ADD CONSTRAINT ROL_PK PRIMARY KEY ( id_rol ) ;

CREATE TABLE SOLICITUD_SERVICIO 
    ( 
     fecha_creacion                DATE  NOT NULL , 
     id_solicitud                  INTEGER  NOT NULL , 
     estado_solicitud              VARCHAR2 (12)  NOT NULL , 
     instrucciones                 VARCHAR2 (255)  NOT NULL , 
     total_compra                  NUMBER  NOT NULL , 
     DETALLE_ORDEN_id_orden        INTEGER  NOT NULL , 
     PAGO_SOLICITUD_id_transaccion INTEGER  NOT NULL , 
     CLIENTE_id_cliente            INTEGER  NOT NULL , 
     TIPO_SOLICITUD_id_tipo_solic  INTEGER  NOT NULL 
    ) 
;
CREATE UNIQUE INDEX SOLICITUD_SERVICIO__IDX ON SOLICITUD_SERVICIO 
    ( 
     PAGO_SOLICITUD_id_transaccion ASC 
    ) 
;
CREATE UNIQUE INDEX SOLICITUD_SERVICIO__IDXv1 ON SOLICITUD_SERVICIO 
    ( 
     DETALLE_ORDEN_id_orden ASC 
    ) 
;

ALTER TABLE SOLICITUD_SERVICIO 
    ADD CONSTRAINT SOLICITUD_SERVICIO_PK PRIMARY KEY ( id_solicitud ) ;

CREATE TABLE TIPO_CUENTA 
    ( 
     id_tipo_cuenta INTEGER  NOT NULL , 
     nombre_tipo    VARCHAR2 (20)  NOT NULL 
    ) 
;

ALTER TABLE TIPO_CUENTA 
    ADD CONSTRAINT TIPO_CUENTA_PK PRIMARY KEY ( id_tipo_cuenta ) ;

CREATE TABLE TIPO_ESPACIO 
    ( 
     id_tipo_espacio   INTEGER  NOT NULL , 
     nombre_tipo       VARCHAR2 (12)  NOT NULL , 
     descripcion       VARCHAR2 (255)  NOT NULL , 
     nivel_complejidad CHAR (2)  NOT NULL 
    ) 
;

ALTER TABLE TIPO_ESPACIO 
    ADD CONSTRAINT TIPO_ESPACIO_PK PRIMARY KEY ( id_tipo_espacio ) ;

CREATE TABLE TIPO_PAGO 
    ( 
     id_tipo_pago                   INTEGER  NOT NULL , 
     nombre_metodo                  VARCHAR2 (50)  NOT NULL , 
     activo                         CHAR (1)  NOT NULL , 
     PAGO_SOLICITUD_id_transaccion  INTEGER  NOT NULL , 
     PAGO_CUIDADOR_id_pago_cuidador INTEGER  NOT NULL 
    ) 
;
CREATE UNIQUE INDEX TIPO_PAGO__IDX ON TIPO_PAGO 
    ( 
     PAGO_SOLICITUD_id_transaccion ASC 
    ) 
;
CREATE UNIQUE INDEX TIPO_PAGO__IDXv1 ON TIPO_PAGO 
    ( 
     PAGO_CUIDADOR_id_pago_cuidador ASC 
    ) 
;

ALTER TABLE TIPO_PAGO 
    ADD CONSTRAINT TIPO_PAGO_PK PRIMARY KEY ( id_tipo_pago ) ;

CREATE TABLE TIPO_SOLICITUD 
    ( 
     id_tipo_solic         INTEGER  NOT NULL , 
     nombre_servicio       VARCHAR2 (100)  NOT NULL , 
     descripcion           VARCHAR2 (255)  NOT NULL , 
     precio_base           NUMBER (10,2)  NOT NULL , 
     duracion_estimada_min NUMBER (3)  NOT NULL , 
     requiere_insumos      SMALLINT  NOT NULL , 
     estado_sv             SMALLINT  NOT NULL 
    ) 
;

ALTER TABLE TIPO_SOLICITUD 
    ADD CONSTRAINT TIPO_SOLICITUD_PK PRIMARY KEY ( id_tipo_solic ) ;

ALTER TABLE ADMINISTRADOR 
    ADD CONSTRAINT ADMINISTRADOR_CEMENTERIO_FK FOREIGN KEY 
    ( 
     CEMENTERIO_id_region,
     CEMENTERIO_id_provincia,
     CEMENTERIO_id_cementerio
    ) 
    REFERENCES CEMENTERIO 
    ( 
     id_region,
     id_provincia,
     id_cementerio
    ) 
;

ALTER TABLE ADMINISTRADOR 
    ADD CONSTRAINT ADMINISTRADOR_PERSONA_FK FOREIGN KEY 
    ( 
     id_persona
    ) 
    REFERENCES PERSONA 
    ( 
     id_persona
    ) 
;

ALTER TABLE CATALOGO_PRODUCTO 
    ADD CONSTRAINT CAT_PROD_PSTO_VENTA_FK FOREIGN KEY 
    ( 
     PUESTO_VENTA_id_puesto
    ) 
    REFERENCES PUESTO_VENTA 
    ( 
     id_puesto
    ) 
;

ALTER TABLE CATALOGO_PRODUCTO 
    ADD CONSTRAINT CATALOGO_PRODUCTO_PRODUCTO_FK FOREIGN KEY 
    ( 
     PRODUCTO_id_producto
    ) 
    REFERENCES PRODUCTO 
    ( 
     id_producto
    ) 
;

ALTER TABLE CEMENTERIO 
    ADD CONSTRAINT CEMENTERIO_COMUNA_FK FOREIGN KEY 
    ( 
     COMUNA_id_comuna
    ) 
    REFERENCES COMUNA 
    ( 
     id_comuna
    ) 
;

ALTER TABLE CEMENTERIO 
    ADD CONSTRAINT CEMENTERIO_HORARIO_FK FOREIGN KEY 
    ( 
     HORARIO_id_horario
    ) 
    REFERENCES HORARIO 
    ( 
     id_horario
    ) 
;

ALTER TABLE CLIENTE 
    ADD CONSTRAINT CLIENTE_PERSONA_FK FOREIGN KEY 
    ( 
     id_persona
    ) 
    REFERENCES PERSONA 
    ( 
     id_persona
    ) 
;

ALTER TABLE COMUNA 
    ADD CONSTRAINT COMUNA_PROVINCIA_FK FOREIGN KEY 
    ( 
     PROVINCIA_id_provincia
    ) 
    REFERENCES PROVINCIA 
    ( 
     id_provincia
    ) 
;

ALTER TABLE CREDENCIAL 
    ADD CONSTRAINT CREDENCIAL_ROL_FK FOREIGN KEY 
    ( 
     ROL_id_rol
    ) 
    REFERENCES ROL 
    ( 
     id_rol
    ) 
;

ALTER TABLE CUENTA_BANCO 
    ADD CONSTRAINT CUENTA_BANCO_CUIDADOR_FK FOREIGN KEY 
    ( 
     CUIDADOR_id_cuidador
    ) 
    REFERENCES CUIDADOR 
    ( 
     id_cuidador
    ) 
;

ALTER TABLE CUENTA_BANCO 
    ADD CONSTRAINT CUENTA_BANCO_TIPO_CUENTA_FK FOREIGN KEY 
    ( 
     TIPO_CUENTA_id_tipo_cuenta
    ) 
    REFERENCES TIPO_CUENTA 
    ( 
     id_tipo_cuenta
    ) 
;

ALTER TABLE CUIDADOR 
    ADD CONSTRAINT CUIDADOR_HORARIO_FK FOREIGN KEY 
    ( 
     HORARIO_id_horario
    ) 
    REFERENCES HORARIO 
    ( 
     id_horario
    ) 
;

ALTER TABLE CUIDADOR 
    ADD CONSTRAINT CUIDADOR_PERSONA_FK FOREIGN KEY 
    ( 
     id_persona
    ) 
    REFERENCES PERSONA 
    ( 
     id_persona
    ) 
;

ALTER TABLE DETALLE_ORDEN 
    ADD CONSTRAINT DETALLE_ORDEN_CALIFICACION_FK FOREIGN KEY 
    ( 
     CALIFICACION_id_calificacion
    ) 
    REFERENCES CALIFICACION 
    ( 
     id_calificacion
    ) 
;

ALTER TABLE DETALLE_ORDEN 
    ADD CONSTRAINT DETALLE_ORDEN_CUIDADOR_FK FOREIGN KEY 
    ( 
     CUIDADOR_id_cuidador
    ) 
    REFERENCES CUIDADOR 
    ( 
     id_cuidador
    ) 
;

ALTER TABLE DETALLE_ORDEN 
    ADD CONSTRAINT DETALLE_ORDEN_PAGO_CUIDADOR_FK FOREIGN KEY 
    ( 
     PAGO_CUIDADOR_id_pago_cuidador
    ) 
    REFERENCES PAGO_CUIDADOR 
    ( 
     id_pago_cuidador
    ) 
;

ALTER TABLE DETALLE_ORDEN 
    ADD CONSTRAINT DETALLE_ORDEN_PRODUCTO_FK FOREIGN KEY 
    ( 
     PRODUCTO_id_producto
    ) 
    REFERENCES PRODUCTO 
    ( 
     id_producto
    ) 
;

ALTER TABLE ESPACIO 
    ADD CONSTRAINT ESPACIO_CEMENTERIO_FK FOREIGN KEY 
    ( 
     CEMENTERIO_id_region,
     CEMENTERIO_id_provincia,
     CEMENTERIO_id_cementerio
    ) 
    REFERENCES CEMENTERIO 
    ( 
     id_region,
     id_provincia,
     id_cementerio
    ) 
;

ALTER TABLE ESPACIO 
    ADD CONSTRAINT ESPACIO_DETALLE_ORDEN_FK FOREIGN KEY 
    ( 
     DETALLE_ORDEN_id_orden
    ) 
    REFERENCES DETALLE_ORDEN 
    ( 
     id_orden
    ) 
;

ALTER TABLE ESPACIO 
    ADD CONSTRAINT ESPACIO_TIPO_ESPACIO_FK FOREIGN KEY 
    ( 
     TIPO_ESPACIO_id_tipo_espacio
    ) 
    REFERENCES TIPO_ESPACIO 
    ( 
     id_tipo_espacio
    ) 
;

ALTER TABLE FALLECIDO 
    ADD CONSTRAINT FALLECIDO_ESPACIO_FK FOREIGN KEY 
    ( 
     ESPACIO_id_espacio
    ) 
    REFERENCES ESPACIO 
    ( 
     id_espacio
    ) 
;

ALTER TABLE PAGO_CUIDADOR 
    ADD CONSTRAINT PAGO_CUIDADOR_CUENTA_BANCO_FK FOREIGN KEY 
    ( 
     CUENTA_BANCO_id_cuenta
    ) 
    REFERENCES CUENTA_BANCO 
    ( 
     id_cuenta
    ) 
;

ALTER TABLE PERSONA 
    ADD CONSTRAINT PERSONA_CREDENCIAL_FK FOREIGN KEY 
    ( 
     CREDENCIAL_id_credencial
    ) 
    REFERENCES CREDENCIAL 
    ( 
     id_credencial
    ) 
;

ALTER TABLE PROVINCIA 
    ADD CONSTRAINT PROVINCIA_REGION_FK FOREIGN KEY 
    ( 
     REGION_id_region
    ) 
    REFERENCES REGION 
    ( 
     id_region
    ) 
;

ALTER TABLE PUESTO_VENTA 
    ADD CONSTRAINT PUESTO_VENTA_CEMENTERIO_FK FOREIGN KEY 
    ( 
     CEMENTERIO_id_region,
     CEMENTERIO_id_provincia,
     CEMENTERIO_id_cementerio
    ) 
    REFERENCES CEMENTERIO 
    ( 
     id_region,
     id_provincia,
     id_cementerio
    ) 
;

ALTER TABLE REGISTRO_EVIDENCIA 
    ADD CONSTRAINT REG_EVIDENC_DTLLE_ORDEN_FK FOREIGN KEY 
    ( 
     DETALLE_ORDEN_id_orden
    ) 
    REFERENCES DETALLE_ORDEN 
    ( 
     id_orden
    ) 
;

ALTER TABLE RETIRO_INSUMO 
    ADD CONSTRAINT RETIRO_INSUMO_DETALLE_ORDEN_FK FOREIGN KEY 
    ( 
     DETALLE_ORDEN_id_orden
    ) 
    REFERENCES DETALLE_ORDEN 
    ( 
     id_orden
    ) 
;

ALTER TABLE SOLICITUD_SERVICIO 
    ADD CONSTRAINT SOLI_SERVIC_DTLLE_ORDEN_FK FOREIGN KEY 
    ( 
     DETALLE_ORDEN_id_orden
    ) 
    REFERENCES DETALLE_ORDEN 
    ( 
     id_orden
    ) 
;

ALTER TABLE SOLICITUD_SERVICIO 
    ADD CONSTRAINT SOLI_SERVIC_PAGO_SOLI_FK FOREIGN KEY 
    ( 
     PAGO_SOLICITUD_id_transaccion
    ) 
    REFERENCES PAGO_SOLICITUD 
    ( 
     id_transaccion
    ) 
;

ALTER TABLE SOLICITUD_SERVICIO 
    ADD CONSTRAINT SOLI_SERVIC_TPO_SOLI_FK FOREIGN KEY 
    ( 
     TIPO_SOLICITUD_id_tipo_solic
    ) 
    REFERENCES TIPO_SOLICITUD 
    ( 
     id_tipo_solic
    ) 
;

ALTER TABLE SOLICITUD_SERVICIO 
    ADD CONSTRAINT SOLICITUD_SERVICIO_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE TIPO_PAGO 
    ADD CONSTRAINT TIPO_PAGO_PAGO_CUIDADOR_FK FOREIGN KEY 
    ( 
     PAGO_CUIDADOR_id_pago_cuidador
    ) 
    REFERENCES PAGO_CUIDADOR 
    ( 
     id_pago_cuidador
    ) 
;

ALTER TABLE TIPO_PAGO 
    ADD CONSTRAINT TIPO_PAGO_PAGO_SOLICITUD_FK FOREIGN KEY 
    ( 
     PAGO_SOLICITUD_id_transaccion
    ) 
    REFERENCES PAGO_SOLICITUD 
    ( 
     id_transaccion
    ) 
;

CREATE OR REPLACE TRIGGER FKNTM_CUENTA_BANCO 
BEFORE UPDATE OF CUIDADOR_id_cuidador 
ON CUENTA_BANCO 
BEGIN 
  raise_application_error(-20225,'Non Transferable FK constraint  on table CUENTA_BANCO is violated'); 
END; 
/

CREATE OR REPLACE TRIGGER FKNTM_DETALLE_ORDEN 
BEFORE UPDATE OF PAGO_CUIDADOR_id_pago_cuidador 
ON DETALLE_ORDEN 
BEGIN 
  raise_application_error(-20225,'Non Transferable FK constraint  on table DETALLE_ORDEN is violated'); 
END; 
/

CREATE OR REPLACE TRIGGER FKNTM_ESPACIO 
BEFORE UPDATE OF DETALLE_ORDEN_id_orden 
ON ESPACIO 
BEGIN 
  raise_application_error(-20225,'Non Transferable FK constraint  on table ESPACIO is violated'); 
END; 
/

CREATE OR REPLACE TRIGGER FKNTO_FALLECIDO 
BEFORE UPDATE OF ESPACIO_id_espacio 
ON FALLECIDO 
FOR EACH ROW 
BEGIN 
 IF :old.ESPACIO_id_espacio IS NOT NULL THEN 
  raise_application_error(-20225,'Non Transferable FK constraint FALLECIDO_ESPACIO_FK on table FALLECIDO is violated'); 
 END IF; 
END; 
/

CREATE OR REPLACE TRIGGER FKNTM_SOLICITUD_SERVICIO 
BEFORE UPDATE OF PAGO_SOLICITUD_id_transaccion, DETALLE_ORDEN_id_orden 
ON SOLICITUD_SERVICIO 
BEGIN 
  raise_application_error(-20225,'Non Transferable FK constraint  on table SOLICITUD_SERVICIO is violated'); 
END; 
/

CREATE OR REPLACE TRIGGER FKNTM_TIPO_PAGO 
BEFORE UPDATE OF PAGO_SOLICITUD_id_transaccion, PAGO_CUIDADOR_id_pago_cuidador 
ON TIPO_PAGO 
BEGIN 
  raise_application_error(-20225,'Non Transferable FK constraint  on table TIPO_PAGO is violated'); 
END; 
/

CREATE OR REPLACE TRIGGER ARC_FKArc_1_CLIENTE 
BEFORE INSERT OR UPDATE OF id_persona 
ON CLIENTE 
FOR EACH ROW 
DECLARE 
    d VARCHAR2 (13); 
BEGIN 
    SELECT A.tipo_persona INTO d 
    FROM PERSONA A 
    WHERE A.id_persona = :new.id_persona; 
    IF (d IS NULL OR d <> 'CLIENTE') THEN 
        raise_application_error(-20223,'FK CLIENTE_PERSONA_FK in Table CLIENTE violates Arc constraint on Table PERSONA - discriminator column tipo_persona doesn''t have value ''CLIENTE'''); 
    END IF; 
    EXCEPTION 
    WHEN NO_DATA_FOUND THEN 
        NULL; 
    WHEN OTHERS THEN 
        RAISE; 
END; 
/

CREATE OR REPLACE TRIGGER ARC_FKArc_1_ADMINISTRADOR 
BEFORE INSERT OR UPDATE OF id_persona 
ON ADMINISTRADOR 
FOR EACH ROW 
DECLARE 
    d VARCHAR2 (13); 
BEGIN 
    SELECT A.tipo_persona INTO d 
    FROM PERSONA A 
    WHERE A.id_persona = :new.id_persona; 
    IF (d IS NULL OR d <> 'ADMINISTRADOR') THEN 
        raise_application_error(-20223,'FK ADMINISTRADOR_PERSONA_FK in Table ADMINISTRADOR violates Arc constraint on Table PERSONA - discriminator column tipo_persona doesn''t have value ''ADMINISTRADOR'''); 
    END IF; 
    EXCEPTION 
    WHEN NO_DATA_FOUND THEN 
        NULL; 
    WHEN OTHERS THEN 
        RAISE; 
END; 
/

CREATE OR REPLACE TRIGGER ARC_FKArc_1_CUIDADOR 
BEFORE INSERT OR UPDATE OF id_persona 
ON CUIDADOR 
FOR EACH ROW 
DECLARE 
    d VARCHAR2 (13); 
BEGIN 
    SELECT A.tipo_persona INTO d 
    FROM PERSONA A 
    WHERE A.id_persona = :new.id_persona; 
    IF (d IS NULL OR d <> 'CUIDADOR') THEN 
        raise_application_error(-20223,'FK CUIDADOR_PERSONA_FK in Table CUIDADOR violates Arc constraint on Table PERSONA - discriminator column tipo_persona doesn''t have value ''CUIDADOR'''); 
    END IF; 
    EXCEPTION 
    WHEN NO_DATA_FOUND THEN 
        NULL; 
    WHEN OTHERS THEN 
        RAISE; 
END; 
/



-- Informe de Resumen de Oracle SQL Developer Data Modeler: 
-- 
-- CREATE TABLE                            28
-- CREATE INDEX                             8
-- ALTER TABLE                             65
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           9
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- CREATE MATERIALIZED VIEW LOG             0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
