# ---- Etapa 1: build ----
# Compila el proyecto con Maven. Esta imagen NO es la que corre en producción,
# solo se usa para generar el .jar; se descarta al final del build.
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos primero el pom.xml solo, para aprovechar la cache de capas de Docker:
# si no cambiaste dependencias, no vuelve a descargar todo el .m2 en cada build.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: runtime ----
# Imagen final: solo el JRE (no el JDK completo, no Maven) - mucho más liviana
# y sin herramientas de build innecesarias en producción.
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos SOLO el jar ya compilado desde la etapa de build
COPY --from=build /app/target/architecture-customer-demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]