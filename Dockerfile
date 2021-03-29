
FROM openjdk:11-jre-slim-buster
ADD /target/mostlytracker-0.0.1-SNAPSHOT.jar mostlytracker-0.0.1-SNAPSHOT.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "mostlytracker-0.0.1-SNAPSHOT.jar"]