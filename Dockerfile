# Etapa 1: Compilación
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar solo pom primero (mejor caché de dependencias)
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código y compilar
COPY src src
RUN mvn package -DskipTests -B -q

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/target/*.jar app.jar

# Carpeta para documentos (persistencia)
# Credenciales OAuth: pasar GOOGLE_CLIENT_ID y GOOGLE_CLIENT_SECRET al ejecutar
RUN mkdir -p /app/documents
ENV APP_DOCUMENTS_PATH=/app/documents

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
