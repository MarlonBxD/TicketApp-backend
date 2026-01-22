FROM gradle:8.14.3-jdk21-ubi-minimal AS build
WORKDIR /home/gradle/

ARG AZURE_ARTIFACTS_USERNAME
ARG AZURE_ARTIFACTS_PAT

COPY gradlew settings.gradle build.gradle ./
COPY gradle/wrapper ./gradle/wrapper


COPY build.gradle settings.gradle gradlew /home/gradle/
COPY src/main/resources/application*.yaml /home/gradle/
COPY src /home/gradle/src
COPY ticket-run.sh /home/gradle/
RUN chmod +x ./gradlew

RUN ./gradlew --no-daemon --refresh-dependencies
RUN ./gradlew clean build -x test --no-daemon --stacktrace --warning-mode all

FROM eclipse-temurin:21-jre
ARG BUILD_VERSION
ARG BINARY_NAME
ENV TZ=America/Bogota
ENV VERSION=$BUILD_VERSION
ENV BINARY_NAME=$BINARY_NAME


RUN apt-get update && apt-get install -y libc6

RUN groupadd -r singularit && useradd -r -g singularit -m -d /etc/singularit  singularit
RUN set -ex; \
    mkdir -p /etc/singularit/config; \
    mkdir /etc/singularit/logs; \
    touch /etc/singularit/logs/singularit.log; \
    chown -R singularit:singularit /etc/singularit; \
    chmod 754 -R /etc/singularit;

COPY --from=build /home/gradle/*.yaml /etc/singularit/config/
COPY --from=build /home/gradle/build/libs/${BINARY_NAME}-${VERSION}.jar /etc/singularit/${BINARY_NAME}-${VERSION}.jar
COPY --from=build /home/gradle/ticket-run.sh /usr/local/bin


RUN set -ex; \
    chown -R singularit:singularit /etc/singularit; \
    chmod a+x /usr/local/bin/singularit-run.sh

ENV SPRING_CONFIG_LOCATION=/etc/singularit/config/
ENV CLASSPATH=/etc/singularit
ENV SPRING_CONFIG_NAME=application
ENV PATH=$PATH:${JAVA_HOME}/bin



USER singularit:singularit
WORKDIR /etc/singularit

ENTRYPOINT ["/bin/bash", "-c", "/usr/local/bin/ticket-run.sh ${BINARY_NAME}-${VERSION}.jar"]

EXPOSE 8080