FROM openjdk:11.0.3-jdk-slim-stretch
COPY build/libs/*.jar user-service-ords.jar
EXPOSE 8080
CMD java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar user-service-ords.jar