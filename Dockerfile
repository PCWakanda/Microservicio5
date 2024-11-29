# Use a base image of Java 21
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR al contenedor
COPY target/Microservicio5-1.0.0.jar /app/microservicio5.jar

# Define el puerto en el que se ejecutará la aplicación
EXPOSE 8084

# Comando para ejecutar la aplicación con el perfil docker
ENTRYPOINT ["java", "-jar", "/app/microservicio5.jar", "--spring.profiles.active=docker"]