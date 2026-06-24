# ============================================================
# Dockerfile
# Ubicación: backend/Dockerfile
#
# QUÉ HACE:
#   Construye la imagen Docker del backend Spring Boot.
#   Usa multi-stage build: dos fases separadas para que la imagen
#   final sea pequeña (solo JRE, sin Maven ni código fuente).
#
# CÓMO SE COMUNICA:
#   docker-compose.yml lo referencia con "build: context: ./backend"
#   y levanta el contenedor a partir de esta imagen.
#   El contenedor expone el puerto 8080 que docker-compose mapea
#   al puerto 8080 del host (EC2 en AWS).
#
# FASES:
#   1. builder → compila el proyecto con Maven y genera el .jar
#   2. runtime → copia solo el .jar y lo ejecuta con JRE (más liviano)
# ============================================================

# ── FASE 1: Compilación ─────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copiamos primero solo pom.xml para aprovechar la caché de Docker.
# Si el código cambia pero las dependencias no, Maven no las vuelve
# a descargar — solo recompila el código fuente.
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw
# Descarga dependencias sin compilar el código fuente todavía
RUN ./mvnw dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ── FASE 2: Imagen de producción ────────────────────────────
# JRE (runtime) es mucho más pequeño que JDK (development kit)
# eclipse-temurin:21-jre-alpine pesa ~85MB vs ~340MB del JDK
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario no-root por seguridad.
# Correr como root dentro del contenedor es mala práctica.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiamos solo el .jar generado en la fase anterior
COPY --from=builder /app/target/*.jar app.jar

# Documentamos el puerto que usa la app (no lo expone, solo documenta)
EXPOSE 8080

# Opciones JVM para contenedores:
# -XX:+UseContainerSupport → JVM detecta límites de CPU/RAM del contenedor
# -XX:MaxRAMPercentage=75  → usa máximo el 75% de la RAM disponible
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", "app.jar"]