FROM openjdk:8
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} datahandler-0.0.1-SNAPSHOT.jar
EXPOSE 8080
EXPOSE 50051
ENTRYPOINT ["java","-jar","/datahandler-0.0.1-SNAPSHOT.jar"]
