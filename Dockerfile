FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} cabd-springboot-jwt.jar
ENTRYPOINT ["java","-jar","cabd-springboot-jwt.jar"]
EXPOSE 8081
