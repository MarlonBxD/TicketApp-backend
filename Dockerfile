FROM gradle:8.8.0-jdk-21-and-22 AS build
WORKDIR /home/gradle/

COPY build.gradle settings.gradle gradlew /home/gradle/
COPY src/main/resources/application*.yaml /home/gradle/
COPY src /home/gradle/src
COPY sic-run.sh /home/gradle/

RUN mkdir /usr/local/jvm
RUN wget https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz
RUN tar zxf openjdk-21.0.2_linux-x64_bin.tar.gz --directory /usr/local/jvm/



RUN gradle --refresh-dependencies --no-daemon
RUN gradle clean build -x test

FROM debian:stable-slim

ENV TZ=America/Bogota
ENV VAULT_TOKEN=root

RUN apt-get update && apt-get install -y libc6

RUN groupadd -r sic && useradd -r -g sic -m -d /etc/sic  sic
RUN set -ex; \
    mkdir -p /etc/sic/config; \
    mkdir /etc/sic/logs; \
    touch /etc/sic/logs/sic.log; \
    chown -R sic:sic /etc/sic; \
    chmod 754 -R /etc/sic; \
    mkdir -p /usr/local/jvm/java;

COPY --from=build /home/gradle/*.yaml /etc/sic/config/
COPY --from=build /home/gradle/build/libs/TestSic-0.0.1.jar /etc/sic/TestSic-0.0.1.jar
COPY --from=build /home/gradle/sic-run.sh /usr/local/bin
COPY --from=build  /usr/local/jvm/*  /usr/local/jvm/java




RUN set -ex; \
    chown -R sic:sic /etc/sic; \
    chmod a+x /usr/local/bin/sic-run.sh



ENV SPRING_CONFIG_LOCATION=/etc/sic/config/
ENV CLASSPATH=/etc/sic
ENV SPRING_CONFIG_NAME=application
ENV JAVA_HOME=/usr/local/jvm/java
ENV PATH=$PATH:${JAVA_HOME}/bin



USER sic:sic
WORKDIR /etc/sic

ENTRYPOINT ["/bin/bash", "-c", "/usr/local/bin/sic-run.sh TestSic-0.0.1.jar"]

EXPOSE 8080