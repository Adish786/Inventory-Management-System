FROM openjdk:17
WORKDIR /api-gateway
COPY target/api-gateway.jar api-gateway.jar
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
EXPOSE:8089