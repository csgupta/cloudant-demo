FROM openjdk:8
EXPOSE 8080
ADD target/cloudant-0.0.1-SNAPSHOT.jar cloudant-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/cloudant-0.0.1-SNAPSHOT.jar"]
