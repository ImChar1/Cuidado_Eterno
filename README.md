# Cuidado Eterno — Backend API

Plataforma de intermediación para la gestión de mantención de sepulturas en cementerios públicos de Chile.
Stack: Java 21 · Spring Boot 3.4.5 · MariaDB · Docker · Terraform · AWS Academy

---

## Índice

1. [Requisitos previos](#1-requisitos-previos)
2. [Variables de entorno](#2-variables-de-entorno)
3. [Levantar en desarrollo local (Docker Compose)](#3-levantar-en-desarrollo-local-docker-compose)
4. [Verificar que todo funciona](#4-verificar-que-todo-funciona)
5. [Respaldo y restauración de la base de datos](#5-respaldo-y-restauración-de-la-base-de-datos)
6. [Despliegue en AWS con Terraform](#6-despliegue-en-aws-con-terraform)
7. [Replicar el ambiente productivo localmente](#7-replicar-el-ambiente-productivo-localmente)
8. [Comandos útiles de mantenimiento](#8-comandos-útiles-de-mantenimiento)

---

## 1. Requisitos previos

Instalar en la máquina local antes de continuar:

| Herramienta    | Versión mínima | Verificar con |

| Docker Desktop | 24.x       | `docker --version` |
| Docker Compose | 2.x        | `docker compose version` |
| Java JDK       | 21         | `java -version` |
| Terraform      | 1.6+       | `terraform -version` |
| Git            | cualquiera | `git --version` |

> **No es necesario instalar Maven ni MariaDB** en el host. Docker los gestiona internamente.

---

## 2. Variables de entorno

Crear el archivo `.env` en la raíz de `Desarrollo/` (mismo nivel que `docker-compose.yml`).
**Este archivo nunca se sube al repositorio** (está en `.gitignore`).

```bash
# Desarrollo/  →  crear archivo .env
DB_ROOT_PASSWORD=rootpass
DB_NAME=cuidado_eterno
DB_USER=ce_user
DB_PASSWORD=ce_pass
JWT_SECRET=cuidadoEternoSecretKeyMustBe32CharsMin!
JWT_EXPIRATION=86400000
SWAGGER_ENABLED=true
```

> En AWS EC2 estas variables se exportan directamente en el sistema operativo:
> ```bash
> export DB_URL=jdbc:mariadb://<IP_PRIVADA_EC2_BD>:3306/cuidado_eterno
> export DB_USER=ce_user
> export DB_PASSWORD=ce_pass
> export JWT_SECRET=<secret_produccion>
> export SWAGGER_ENABLED=false
> ```

---

## 3. Levantar en desarrollo local (Docker Compose)

Todos los comandos se ejecutan desde la carpeta `Desarrollo/`.

### 3.1 Primera vez (construir imagen y levantar)

```bash
# Construir la imagen del backend y levantar ambos contenedores
docker compose up --build -d

#Otros comandos
docker compose restart        # → scripts NO corren solo se reinician la imagenes ya construidas.

```

Docker Compose hace lo siguiente en orden:
1. Levanta el contenedor `db` (MariaDB 11)
2. Espera el healthcheck de la BD (`healthcheck.sh --connect`)

  -docker compose up --build d db (db es el nombre del servicio en el docker-compose).

  -docker compose logs -f db (para verificar "[NOTE] mariadbd: redy for connections).
  
  -verificar si se crearon las tablas correctamente por el DDL con:
  [docker exec -it cuidado_eterno_db mariadb -u ce_user -pce_pass cuidado_eterno -e "SHOW TABLES;"] PARA POWERSHELL.
  
  -verificar si se poblaron las tablas correctamente por el script DML con:
  [docker exec -it cuidado_eterno_db mariadb -u ce_user -pce_pass cuidado_eterno -e "SELECT nombre_rol FROM rol;"] PARA POWERSHELL
  
  -Para conectarse a la BD y explorar realizando cualquier tipo de Query:
  [docker exec -it cuidado_eterno_db mariadb -u ce_user -pce_pass cuidado_eterno] PARA POWERSHELL (salir con exit).

3. Levanta `backend` (Spring Boot) solo cuando la BD está lista
  -docker compose up --build -d backend (para construir el servicio del backend en el docker-compose)
  -docker compose logs -f backend (para verificar el "")

### 3.2 Arranques posteriores (imagen ya construida)

```bash
docker compose up -d
```

### 3.3 Ver logs en tiempo real

```bash
# Todos los servicios
docker compose logs -f

# Solo el backend
docker compose logs -f backend

# Solo la base de datos
docker compose logs -f db
```

### 3.4 Detener los contenedores

```bash
# Detener sin borrar datos
docker compose down

# Detener Y borrar el volumen (borra todos los datos de la BD)
docker compose down -v
```

### 3.5 Reiniciar solo el backend (sin tocar la BD)

```bash
docker compose restart backend
```

### 3.6 Reconstruir la imagen después de cambios en el código

```bash
docker compose up --build -d backend
```

### 3.7 Construir la imagen borrando el cache y luego levantar las imagenes.

```
docker compose build --no-cache
docker compose up -d
```

---

## 4. Verificar que todo funciona

### 4.1 Confirmar que los contenedores están corriendo

```bash
docker ps
```

Debe mostrar dos contenedores:
- `cuidado_eterno_db` — estado `healthy`
- `cuidado_eterno_backend` — estado `Up`

### 4.2 Probar la API con curl

```bash
# Health check básico (debe responder 200 o 404 conocido)
curl http://localhost:8080/api/v1/auth/login

# Login de prueba (reemplazar credenciales)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"admin","clave":"password123"}'
```

### 4.3 Abrir Swagger UI

```
http://localhost:8080/api/v1/swagger-ui/swagger-ui/index.html
```

### 4.4 Conectarse a la BD desde DBeaver / TablePlus

| Campo | Valor |
|---|---|
| Host | `localhost` |
| Puerto | `3306` |
| Base de datos | `cuidado_eterno` |
| Usuario | `ce_user` |
| Contraseña | `ce_pass` |

---

## 5. Respaldo y restauración de la base de datos

### 5.1 Crear un respaldo (dump)

El respaldo se hace **dentro del contenedor** usando `mariadb-dump` (el equivalente a `mysqldump` en MariaDB):

```bash
# Genera el archivo respaldo.sql en la carpeta actual del host
docker exec cuidado_eterno_db \
  mariadb-dump \
  -u ce_user \
  -pce_pass \
  cuidado_eterno > respaldo_$(date +%Y%m%d_%H%M%S).sql
```

> **Por qué dentro del contenedor:** MariaDB no está instalado en el host, solo corre dentro del contenedor Docker. Con `docker exec` ejecutamos el comando en el contexto del contenedor donde sí existe el cliente.

### 5.2 Verificar que el respaldo es válido

```bash
# Ver las primeras líneas del archivo generado
head -20 respaldo_*.sql
```

Debe mostrar el encabezado de MariaDB Dump y las sentencias `CREATE TABLE`.

### 5.3 Restaurar un respaldo

```bash
# Restaurar desde un archivo .sql existente
docker exec -i cuidado_eterno_db \
  mariadb \
  -u ce_user \
  -pce_pass \
  cuidado_eterno < respaldo_20250101_120000.sql
```

### 5.4 Respaldo automatizado en AWS EC2 (producción)

En la instancia EC2 de base de datos, configurar un cron que ejecute el dump diariamente:

```bash
# Abrir el crontab del sistema
crontab -e

# Agregar esta línea (respaldo cada día a las 2:00 AM)
0 2 * * * mariadb-dump -u ce_user -pce_pass cuidado_eterno > /backups/cuidado_eterno_$(date +\%Y\%m\%d).sql
```

---

## 6. Despliegue en AWS con Terraform

La infraestructura se divide en dos etapas en `infra/etapa_1/` e `infra/etapa_2/`. Utilizamos una arquitectura de red privada, por lo que el acceso a los servidores de Aplicación y Base de Datos se realiza a través de un servidor Proxy (Bastion Host).

### 6.1 Prerrequisitos AWS

```bash
# Configurar credenciales de AWS Academy en el archivo de credenciales
# (las credenciales se obtienen desde el portal de AWS Academy → Launch AWS Academy Learner Lab)
aws configure
# AWS Access Key ID: <pegar desde portal>
# AWS Secret Access Key: <pegar desde portal>
# Default region name: us-east-1
# Default output format: json

### 6.2 Etapa 1 — Red y seguridad (VPC, subnets, security groups)

cd infra/etapa_1

# Inicializar Terraform (descarga providers de AWS)
terraform init

# Ver qué recursos va a crear (dry-run, no crea nada)
terraform plan

# Aprovisionar la infraestructura de red
terraform apply
# Escribir "yes" cuando lo pida

terraform state rm aws_s3_bucket_ownership_controls.evidencias_acl
#Para errores de cache sobre los recurso s3 creado en otros intentos.

### 6.3 Etapa 2 — Instancias EC2 y S3

cd ../etapa_2

terraform init
terraform plan

# Aprovisionar las instancias EC2 y el bucket S3
terraform apply
# Escribir "yes" cuando lo pida

Al finalizar, `terraform apply` muestra los outputs: IPs públicas de las instancias y nombre del bucket.

### 6.4 Despliegue del backend en la EC2

```bash
Dado que el backend está en una subred privada, debes conectarte haciendo un salto (Jump) a través del Proxy público.

1. Descarga tu llave .pem desde AWS Academy (por defecto usa la llave 'vockey').
2. Ajusta los permisos de tu llave (solo Mac/Linux):
   chmod 400 ruta/a/tu/llave.pem

3. Conéctate a la db y al backend saltando por el proxy:

  # Ejemplo conectando al Backend:
  ssh -o ProxyCommand="ssh -W %h:%p -i C:/Ruta/A/Tu/llave.pem ec2-user@<IP_PUBLICA_PROXY>" -i C:/Ruta/A/Tu/llave.pem ec2-user@<IP_PRIVADA_BACKEND>

  # Ejemplo conectando a la BD:
  ssh -o ProxyCommand="ssh -W %h:%p -i C:/Ruta/A/Tu/llave.pem ec2-user@<IP_PUBLICA_PROXY>" -i C:/Ruta/A/Tu/llave.pem ec2-user@<IP_PRIVADA_BD>

  ssh -o ProxyCommand="ssh -W %h:%p -i C:/Users/Krlos/Downloads/labsuser.pem ec2-user@3.220.231.217" -i C:/Users/Krlos/Downloads/labsuser.pem ec2-user@10.0.2.111

  # En la EC2: clonar el repositorio
  git clone https://github.com/<usuario>/Cuidado_Eterno.git
  cd Cuidado_Eterno/Desarrollo
  git checkout develop

  ### 📄 Configuración de Archivos `.env`

  Para que la arquitectura separada funcione correctamente, debes crear un archivo `.env` en la ruta `Cuidado_Eterno/Desarrollo/` en cada servidor según corresponda:

  #### 1. En el Servidor de Base de Datos (`db`)
  Este archivo configura las credenciales exclusivas con las que se inicializará el contenedor de MariaDB en su propia máquina.

  ```properties
  # ==========================================
  # CONFIGURACIÓN - SERVIDOR DE BASE DE DATOS
  # ==========================================
  DB_NAME=cuidado_eterno
  DB_USER=ce_user
  DB_PASSWORD=ce_pass
  DB_ROOT_PASSWORD=un_password_seguro_root

  # ==========================================
  # CONFIGURACIÓN - SERVIDOR DE BACKEND
  # ==========================================

  # Conexión a la BD (Reemplaza con la IP_PRIVADA de tu servidor de BD)
  DB_URL=jdbc:mariadb://<IP_PRIVADA_EC2_BD>:3306/cuidado_eterno
  DB_USER=ce_user
  DB_PASSWORD=ce_pass

  # Mapeo obligatorio para consistencia de Docker Compose
  DB_NAME=cuidado_eterno
  DB_ROOT_PASSWORD=un_password_seguro_root

  # Configuración de Seguridad de la API
  JWT_SECRET=tuSuperSecretoAqui32CaracteresMinimo
  JWT_EXPIRATION=86400000
  SWAGGER_ENABLED=true

  # Configuración de Amazon S3
  AWS_S3_REGION=us-east-1
  AWS_S3_BUCKET=cuidado-eterno-duoc-puente-bucket-s3-v2

  nano .env

  #Despliegue de Servicios (Monorepo)
  Utilizamos el mismo repositorio para ambos servidores, pero levantamos únicamente el contenedor correspondiente en cada máquina utilizando variables de entorno locales (.env).
  
  Servidor ------------------/-------- Rol ----------/------- Comando Docker a ejecutar
  Servidor 3 (IP Privada BD) / Base de Datos MariaDB /sudo docker-compose up --build -d db
  Servidor 2 (IP Privada Backend)API / Spring Boot  /sudo docker-compose up --build -d backend

ssh -i "ruta/a/tu-llave.pem" ec2-user@<TU_IP_PUBLICA_O_DOMINIO> #Para entrar
#a la instancia del proxy y ver errores.*/
sudo tail -f /var/log/nginx/error.log
sudo tail -f /var/log/nginx/access.log

###Comandos para navegar la bd

docker exec -it nombre_de_tu_contenedor_db mysql -u root -p

SHOW DATABASES;

USE nombre_de_tu_base_de_datos;

SHOW TABLES;

DESCRIBE usuario;

### 6.5 Destruir la infraestructura (liberar créditos AWS Academy)
```bash
# Primero etapa 2, luego etapa 1 (orden inverso al aprovisionamiento)
cd infra/etapa_2 && terraform destroy
cd ../etapa_1 && terraform destroy
```

---

## 7. Replicar el ambiente productivo localmente

Para simular exactamente el ambiente de AWS en local, usar las mismas variables de entorno que producción pero apuntando a los contenedores locales:

```bash
# Crear .env.prod con valores de producción simulados
cat > .env.prod << EOF
DB_ROOT_PASSWORD=prodRootPass!
DB_NAME=cuidado_eterno
DB_USER=ce_user_prod
DB_PASSWORD=ProdPassword!123
JWT_SECRET=ProdSecretKeyMustBeAtLeast32CharsLong!
JWT_EXPIRATION=86400000
SWAGGER_ENABLED=false
EOF

# Levantar con el archivo de variables de producción
docker compose --env-file .env.prod up --build -d
```

La diferencia clave con el ambiente de desarrollo: `SWAGGER_ENABLED=false` deshabilita Swagger UI, tal como en producción.

---

## 8. Comandos útiles de mantenimiento

```bash
# Ver uso de recursos de los contenedores (CPU, RAM, red)
docker stats

# Entrar al contenedor del backend (shell interactivo)
docker exec -it cuidado_eterno_backend sh

# Entrar al cliente de MariaDB directamente
docker exec -it cuidado_eterno_db mariadb -u ce_user -pce_pass cuidado_eterno

# Ver tablas de la base de datos
docker exec -it cuidado_eterno_db mariadb -u ce_user -pce_pass cuidado_eterno -e "SHOW TABLES;"

# Ver el espacio que ocupa la imagen Docker del backend
docker images | grep cuidado

# Limpiar imágenes y contenedores sin usar (liberar espacio en disco)
docker system prune -f

# Ver la versión de la BD corriendo
docker exec cuidado_eterno_db mariadb --version
```

---

## Estructura del proyecto

```
Desarrollo/
├── .env                          ← Variables de entorno (NO subir a Git)
├── docker-compose.yml            ← Orquestación local (BD + Backend)
├── backend/
│   ├── Dockerfile                ← Multi-stage build (JDK builder → JRE runtime)
│   ├── pom.xml                   ← Java 21, Spring Boot 3.4.5, dependencias
│   └── src/main/
│       ├── java/com/cuidadoeterno/backend/
│       │   ├── config/           ← JwtConfig, SecurityConfig, TransbankConfig
│       │   ├── modules/          ← cementerio | finanzas | inventario | servicio | usuario
│       │   └── shared/           ← exception, response, security (JwtUtil, filtros)
│       └── resources/
│           └── application.yml   ← Configuración Spring Boot (datasource, JWT, Swagger)
├── frontend/                     ← App Android (Kotlin + MVVM + Jetpack Compose)
└── infra/
    ├── Script_bd/
    │   └── cuidado_eterno_mysql.sql  ← DDL: crea todas las tablas al iniciar la BD
    ├── etapa_1/
    │   ├── main.tf               ← VPC, subnets, security groups
    │   ├── variables.tf
    │   └── outputs.tf
    └── etapa_2/
        ├── main.tf               ← EC2 backend, EC2 BD, S3 bucket
        ├── variables.tf
        └── outputs.tf
```
