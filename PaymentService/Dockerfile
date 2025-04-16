FROM openjdk:17
WORKDIR /Payment-Service
COPY target/Payment-Service.jar Payment-Service.jar
ENTRYPOINT ["java", "-jar", "paymentService.jar"]
EXPOSE:8003
