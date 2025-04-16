FROM openjdk:17
WORKDIR /Inventory-Service
COPY target/Inventory-Service.jar Inventory-Service.jar
ENTRYPOINT ["java", "-jar", "Inventory-Service.jar"]
EXPOSE:8006