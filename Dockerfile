#'FROM' can be changed for 'store/oracle/jdk:11' if your account have active subscription for oracle jdk11 https://hub.docker.com/_/oracle-jdk
# and you have to be logged in with this acc in 'Docker Desktop' software, otherwise authorization error occurs
FROM openjdk:11
ARG JAR_FILE=target/hostel-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.activemq.broker-url=tcp://activemq:61616"]