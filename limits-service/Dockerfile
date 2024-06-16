FROM openjdk:21-jdk
EXPOSE 8080
COPY target/*.jar shorted-server.jar
ENTRYPOINT ["java","-jar","/shorted-server.jar"]