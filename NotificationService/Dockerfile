FROM openjdk:17
WORKDIR /notification-service
COPY target/notification-service.jar notification-service.jar
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
EXPOSE:8009
