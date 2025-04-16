FROM openjdk:17
WORKDIR /security-service
COPY target/security-service.jar security-service.jar
ENTRYPOINT ["java", "-jar", "security-service.jar"]
EXPOSE:8088
