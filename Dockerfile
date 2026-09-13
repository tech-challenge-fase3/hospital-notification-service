FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY hospital-parent/pom.xml hospital-parent/pom.xml
COPY hospital-notification-service/pom.xml hospital-notification-service/pom.xml
COPY hospital-notification-service/.mvn hospital-notification-service/.mvn
COPY hospital-notification-service/mvnw hospital-notification-service/mvnw
COPY hospital-notification-service/src hospital-notification-service/src
RUN chmod +x hospital-notification-service/mvnw \
    && ./hospital-notification-service/mvnw -f hospital-notification-service/pom.xml clean package -DskipTests \
    && cp /workspace/hospital-notification-service/target/notification-service-0.0.1-SNAPSHOT.jar /workspace/app.jar

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
