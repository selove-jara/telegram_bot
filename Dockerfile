# Этап сборки
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml . 
COPY src ./src
RUN mvn clean package -DskipTests

# Этап запуска
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/market_parsing-1.0-SNAPSHOT.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "/app/app.jar"]