FROM maven:3.9.6-amazoncorretto-21 AS build

WORKDIR /app
COPY pom.xml .
COPY shop ./shop
COPY entity ./entity
COPY auth-rpac ./auth-rpac
COPY base ./base
RUN mvn -B -q -DskipTests -pl shop -am package

# create Image
FROM amazoncorretto:21.0.4
WORKDIR /app
COPY --from=build /app/shop/target/*.jar shop.jar
ENV SPRING_PROFILES_ACTIVE=dev
ENV SPRING_DATASOURCE_URL=jdbc:mysql://app-mobile-mysql:3306/ecom
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root

ENTRYPOINT ["java", "-jar", "shop.jar"]