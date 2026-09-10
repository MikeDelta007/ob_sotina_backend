# Utiliser une image de base OpenJDK maintenue (openjdk:*-alpine est déprécié/retiré de Docker Hub)
FROM eclipse-temurin:17-jre-alpine

# Créer un répertoire pour l'application
WORKDIR /app

# Copier l'archive générée dans le conteneur (packaging WAR exécutable via spring-boot-maven-plugin)
COPY target/o-b.war /app/app.war

# Exposer le port sur lequel l'application va tourner (ex: 8080)
EXPOSE 8080

# Démarrer l'application
ENTRYPOINT ["java", "-jar", "/app/app.war"]

