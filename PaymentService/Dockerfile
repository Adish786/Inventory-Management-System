FROM openjdk:17
WORKDIR /payment-service
COPY target/payment-service.jar payment-service.jar
ENTRYPOINT ["java", "-jar", "payment-service.jar"]
EXPOSE:8003
