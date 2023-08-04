FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/role-*-SNAPSHOT.jar
COPY ${JAR_FILE} role.jar
ENTRYPOINT ["java","-jar","/role.jar"]
EXPOSE 80