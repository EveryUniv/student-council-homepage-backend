FROM openjdk:11

WORKDIR /usr/app/

COPY build/libs/*.jar application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=\"Asia/Seoul\"", "-jar", "application.jar", "--spring.config.location=file:///usr/app/application.yml"]