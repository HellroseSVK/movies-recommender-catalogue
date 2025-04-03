# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY movie-service/pom.xml movie-service/pom.xml
COPY shared/pom.xml shared/pom.xml

RUN sed -i '/<module>/ { /<module>movie-service<\/module>/! { /<module>shared<\/module>/!d } }' pom.xml
RUN mvn dependency:go-offline

COPY movie-service movie-service
COPY shared shared

RUN mvn package -DskipTests
RUN cp movie-service/target/*.jar app.jar

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/app.jar .

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
