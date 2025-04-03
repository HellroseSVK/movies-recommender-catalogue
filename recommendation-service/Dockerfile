# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY recommendation-service/pom.xml recommendation-service/pom.xml
COPY shared/pom.xml shared/pom.xml

RUN sed -i '/<module>/ { /<module>recommendation-service<\/module>/! { /<module>shared<\/module>/!d } }' pom.xml
RUN mvn dependency:go-offline

COPY recommendation-service recommendation-service
COPY shared shared

RUN mvn package -DskipTests
RUN cp recommendation-service/target/*.jar app.jar

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/app.jar .

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
