FROM openjdk:17
WORKDIR /eureka-server
COPY target/eureka-server.jar eureka-server.jar
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]
EXPOSE:8761