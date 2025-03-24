# -------- BUILD STAGE --------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests && \
    cp target/*.jar app.jar

# -------- RUNTIME STAGE --------
FROM eclipse-temurin:21.0.2_13-jre-jammy
WORKDIR /app
COPY --from=build /app/app.jar .
COPY /etc/movie.properties /app/etc/application.properties
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
