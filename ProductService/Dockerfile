FROM openjdk:17
WORKDIR /product-service
COPY target/product-service.jar product-service.jar
ENTRYPOINT ["java", "-jar", "product-service.jar"]
EXPOSE:8005
