FROM won983212/pinpoint-agent-jdk-11

WORKDIR /usr/app/

COPY build/libs/*.jar application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=\"Asia/Seoul\"", "-jar",\
    "-javaagent:./pinpoint/pinpoint-bootstrap-2.5.0.jar",\
    "-Dpinpoint.agentId=dev",\
    "-Dpinpoint.applicationName=dku-council-back-dev",\
    "-Dpinpoint.config=./pinpoint/pinpoint-root.config",\
    "application.jar",\
    "--spring.config.location=file:///usr/app/application.yml"]