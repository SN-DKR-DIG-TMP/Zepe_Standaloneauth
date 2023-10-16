FROM openjdk:8 as base

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
COPY src ./src

FROM base as development
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=postgresql"]


FROM base as test
RUN ["./mvnw", "test"]

FROM base as build
RUN ./mvnw package

FROM openjdk:8-jre-slim as production
EXPOSE 8081

COPY --from=build /app/target/*-*.jar /standaloneauth.jar

CMD ["java", "-jar", "/standaloneauth.jar"]