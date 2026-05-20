FROM maven:3.9.6-amazoncorretto-21 AS build

WORKDIR /app
COPY pom.xml .
COPY crm ./crm
COPY entity ./entity
COPY base ./base
RUN mvn -B -q -DskipTests -pl crm -am package

# create Image
FROM amazoncorretto:21.0.4
WORKDIR /app
COPY --from=build /app/crm/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "app.jar"]