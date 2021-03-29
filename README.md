# Mostly Tracker - Project management system
1. APIs developed:
  1. Project
    - POST http://localhost:9090/project - create project
    - GET  http://localhost:9090/project - get all projects
    - GET  http://localhost:9090/project/{projectId} - get a specified project
    - PATCH http://localhost:9090/project/{projectId} - update a specified project
    - DELETE  http://localhost:9090/project/{projectId} - delete a specified project
    - GET  http://localhost:9090/project/summary/{projectId} - get summary of a specified project
  2. Project entry
    - POST  http://localhost:9090/project/entry - create project entry
    - DELETE  http://localhost:9090/project/entry/{projectEntryId} - delete a project entry
    - GET  http://localhost:9090/project/entry/{projectId} - get all project entries of a specified project
  
2. Added OPENAPI specification (Swagger) --> http://localhost:9090/swagger-ui/index.html
3. Both API & Swagger are secured using SpringSecurity BasicAuth
  - API credentials --> USERNAME: tracker_user & PASSWORD: tracker_pass
  - Swagger credentials --> USERNAME: swagger_user & PASSWORD: swagger_password
  - Kindly trim the credentials before using.
4. TestContainers are added to avoid dependencies on the actual PSQL container.
  - Can execute the tests using "mvn test" command(Kindly ensure to be on the working directory before executing the task)

Steps to run the application:
=============================
- Docker compose version used - 3.7
- Kindly ensure that docker and docker-compose is installed
- Run application locally
  - Execute docker-compose -f docker-compose-only-db.yml up (to run PSQL container in the docker )
  - Modify application.yml => tracker.db.url => change jdbcurl host to localhost
  - Run the application locally using "mvn spring-boot:run" command
- Run application in the docker
  - Make sure application.yml => tracker.db.url => host = postgres(service name in the docker-compose.yml)
  - Execute docker-compose -f docker-compose-full.yml up 
  
Services in docker-compose:

postgres -- To pull and start the PSQL container
mostlytracker -- Prepare java application image and start it by exposing the 9090 port to localhost 9090
