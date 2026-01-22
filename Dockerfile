# Etapa 1: Build
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copiar archivos de gradle
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Descargar dependencias (cache layer)
RUN ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN ./gradlew bootJar -x test --no-daemon

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
