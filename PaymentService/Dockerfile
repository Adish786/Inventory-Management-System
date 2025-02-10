FROM openjdk:17
WORKDIR /paymentService
COPY target/paymentService.jar paymentService.jar
ENTRYPOINT ["java", "-jar", "paymentService.jar"]
EXPOSE:8092
