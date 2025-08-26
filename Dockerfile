# Build stage
FROM docker.1ms.run/maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml mvnw .
COPY .mvn .mvn
COPY package.json package-lock.json* .
COPY src src
# Install dependencies and build application
RUN mkdir -p /root/.m2 && cp .mvn/settings.xml /root/.m2/settings.xml \
    && chmod +x mvnw && ./mvnw -Pprod -DskipTests package

# Runtime stage
FROM docker.1ms.run/eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
