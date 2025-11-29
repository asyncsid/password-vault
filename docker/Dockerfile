FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/password-vault.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]