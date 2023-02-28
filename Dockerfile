FROM openjdk:11
WORKDIR .
COPY build/libs/sys-golf-etl-0.0.1.jar /usr/local/sys-golf-etl-0.0.1.jar
EXPOSE 8080
COPY config/docker/start.sh /usr/local/start.sh
RUN ["chmod", "+x", "/usr/local/start.sh"]
ENTRYPOINT ["/usr/local/start.sh"]

