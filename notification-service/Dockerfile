FROM cr.gitlab.switch.ch/hslu/shared/devops/docker-cache/amazoncorretto:21.0.4-alpine
ARG CI_GIT_VERSION
ARG CI_GIT_DATE

# add jar to image
RUN echo "$CI_GIT_VERSION,$CI_GIT_DATE" > version.txt
COPY ./target/service.jar service.jar

# Startup
CMD java ${JAVA_OPTS} -jar ./service.jar
