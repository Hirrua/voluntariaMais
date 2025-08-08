#Gerar somente o .jar
FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#Carregar o .jar e Java
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
#Copia o .jar que foi gerado anteriormente
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
