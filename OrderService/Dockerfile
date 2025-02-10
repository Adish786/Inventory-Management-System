FROM openjdk:17
WORKDIR /order-service
COPY target/order-service.jar order-service.jar
ENTRYPOINT ["java", "-jar", "order-service.jar"]
EXPOSE:8093
