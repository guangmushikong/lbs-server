FROM centos/java8
ADD lbs-server-1.0-SNAPSHOT.jar lbs-server-1.0-SNAPSHOT.jar
ADD bootstrap.yml bootstrap.yml
ENV LANG en_US.utf8
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN bash -c 'touch /lbs-server-1.0-SNAPSHOT.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","/lbs-server-1.0-SNAPSHOT.jar"]