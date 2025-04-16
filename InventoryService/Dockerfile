FROM openjdk:17
WORKDIR /inventory-service
COPY target/inventory-service.jar inventory-service.jar
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]
EXPOSE:8006