
FROM openjdk:8-jdk-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ADD target/gs-messaging-rabbitmq-0.1.0.jar initial.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar initial.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar initial.jar
