FROM openjdk:11.0.3-jdk-slim-stretch
COPY build/libs/*.jar user-service-ords.jar
EXPOSE 8080
CMD java -XX:+FlightRecorder -XX:StartFlightRecording=delay=20s,duration=120s,name=Test,filename=recording.jfr,settings=profile -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar user-service-ords.jar