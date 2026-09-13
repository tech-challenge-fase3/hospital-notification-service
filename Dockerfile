FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY hospital-parent/pom.xml hospital-parent/pom.xml
COPY hospital-notification-service/pom.xml hospital-notification-service/pom.xml
COPY hospital-notification-service/.mvn hospital-notification-service/.mvn
COPY hospital-notification-service/mvnw hospital-notification-service/mvnw
COPY hospital-notification-service/src hospital-notification-service/src
RUN chmod +x hospital-notification-service/mvnw \
    && ./hospital-notification-service/mvnw -f hospital-notification-service/pom.xml clean package -DskipTests \
    && cd hospital-notification-service/target \
    && jar xf app.jar META-INF/MANIFEST.MF \
    && grep -q 'Main-Class: org.springframework.boot.loader.launch.JarLauncher' META-INF/MANIFEST.MF

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/hospital-notification-service/target/app.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
