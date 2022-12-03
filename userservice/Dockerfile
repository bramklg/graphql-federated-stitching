FROM gradle:7.5.1-jdk17-focal as builder
WORKDIR /app
COPY . .
RUN gradle bootJar

FROM eclipse-temurin:17-focal

RUN mkdir -p /app
WORKDIR /app
COPY --from=builder /app/build/libs/service.jar /app/service.jar

ENTRYPOINT ["java", "-jar", "/app/service.jar"]
