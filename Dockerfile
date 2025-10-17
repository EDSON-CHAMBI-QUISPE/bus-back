# Etapa de construcción
FROM gradle:8.10-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

# Etapa de ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
