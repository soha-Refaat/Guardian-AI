FROM eclipse-temurin:24-jdk-alpine AS build

# install maven
RUN apk add --no-cache maven

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


FROM eclipse-temurin:24-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]