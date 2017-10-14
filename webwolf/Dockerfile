FROM openjdk:8-jre-slim

RUN useradd --home-dir /home/webwolf --create-home -U webwolf

USER webwolf
RUN cd /home/webwolf/
COPY target/webwolf-8.0-SNAPSHOT.jar /home/webwolf/webwolf.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/webwolf/webwolf.jar"]