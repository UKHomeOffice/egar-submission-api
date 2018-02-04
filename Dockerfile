#FROM quay.io/ukhomeofficedigital/openjdk8-jre:v0.2.0
FROM openjdk:8-jdk-alpine
 
# We added a VOLUME pointing to "/tmp" because that is where a Spring Boot application creates working directories for Tomcat by default.
# The effect is to create a temporary file on your host under "/var/lib/docker" and link it to the container under "/tmp".
VOLUME /tmp
 
# The Jarfile being packaged
ARG JAR_FILE
 
# Add the built JAR to the target folder
COPY ${JAR_FILE} /opt/egar/lib/egar-service.jar
 
# Set the working directory for any RUN, CMD, ENTRYPOINT, COPY and ADD instructions that follow it.
WORKDIR /opt/egar/lib
 
# Create the settings/config file
# SpringBoot will load properties from application.properties files in a number of locations and add them to the Spring Environment.
RUN mkdir config && \
    touch config/application.properties && \
    echo 'server.port=8080' >> config/application.properties
 
# Expose the port the service
EXPOSE 8080
 
# To reduce Tomcat startup time we added a system property pointing to "/dev/urandom" as a source of entropy.
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/egar/lib/egar-service.jar"]
