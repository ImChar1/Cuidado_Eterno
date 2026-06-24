# ============================================================
# infra/db/Dockerfile
#
# QUÉ HACE:
#   Construye una imagen personalizada de MariaDB 11 que ya
#   incluye los scripts SQL dentro de la imagen.
#
#   Ventaja sobre montar volúmenes en docker-compose:
#   Los scripts viajan dentro de la imagen. Cualquier EC2 que
#   descargue esta imagen desde ECR puede inicializar la BD
#   sin necesitar los archivos .sql en el sistema de archivos
#   local. La imagen es autosuficiente.
#
# CÓMO FUNCIONA docker-entrypoint-initdb.d/:
#   La imagen oficial de MariaDB ejecuta automáticamente todos
#   los archivos .sql de esa carpeta SOLO la primera vez que
#   arranca con un volumen vacío. En arranques posteriores los
#   ignora para no sobreescribir datos existentes.
#   Los archivos se ejecutan en orden alfabético, por eso el
#   prefijo 01_ y 02_ garantiza que el schema va antes que el seed.
#
# CONTEXTO DE BUILD (docker-compose.yml):
#   context: .             <- raíz de Desarrollo/
#   dockerfile: infra/db/Dockerfile
#   Los COPY usan rutas relativas desde ese contexto (Desarrollo/).
#
# USO LOCAL para probar la imagen aislada:
#   docker build -f infra/db/Dockerfile -t cuidado-eterno-db .
#   docker run -d \
#     --name test_db \
#     -e MARIADB_ROOT_PASSWORD=rootpass \
#     -e MARIADB_DATABASE=cuidado_eterno \
#     -e MARIADB_USER=ce_user \
#     -e MARIADB_PASSWORD=ce_pass \
#     -p 3306:3306 \
#     cuidado-eterno-db
#   docker logs test_db
#   docker exec -it test_db mariadb -u ce_user -pce_pass cuidado_eterno -e "SHOW TABLES;"
#   docker rm -f test_db
#
# PRODUCCIÓN (AWS ECR):
#   La imagen se sube a ECR y la EC2 de BD la descarga con
#   docker pull. El volumen de datos (db_data) sigue siendo
#   externo para que los datos persistan entre reinicios.
# ============================================================

FROM mariadb:11

# Copiar los scripts SQL dentro de la imagen.
# Rutas relativas al contexto definido en docker-compose (Desarrollo/).
COPY ./infra/script_db/cuidado_eterno_mariadb_DDL.sql /docker-entrypoint-initdb.d/01_schema.sql
COPY ./infra/script_db/seed_data_DML.sql            /docker-entrypoint-initdb.d/02_seed.sql

# El volumen de datos es externo.
# MariaDB almacena los datos en /var/lib/mysql.
# Docker Compose monta db_data aquí para que los datos
# sobrevivan a reinicios del contenedor.
VOLUME /var/lib/mysql