# Étape 1 : Build du projet avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application
FROM eclipse-temurin:21-jdk
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]