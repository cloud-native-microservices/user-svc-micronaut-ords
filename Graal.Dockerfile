FROM oracle/graalvm-ce:19.1.0 as graalvm
RUN gu install native-image
COPY . /home/app/user-service-ords
WORKDIR /home/app/user-service-ords
RUN native-image --no-server --no-fallback -cp build/libs/user-service-ords-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/user-service-ords/user-service-ords .
RUN mkdir resources
COPY --from=graalvm /opt/graalvm-ce-19.1.0/jre/lib/amd64/libsunec.so /resources
ENV LIBSUNEC_PATH /resources
ENTRYPOINT ["./user-service-ords", "-XX:+PrintGC", "-XX:+PrintGCTimeStamps", "-XX:+VerboseGC", "+XX:+PrintHeapShape", "-Xmx64m"]