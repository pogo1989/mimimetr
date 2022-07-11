FROM maven:3.8-openjdk-18 AS build
ADD . /home/app/
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:18
COPY --from=build /home/app/target/mimimeter-0.0.1-SNAPSHOT.jar /usr/local/lib/mimimetr.jar
COPY --from=build /home/app/src/main/resources/certs/sometest.cer /home/certs/sometest.cer
RUN keytool -cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias sometest -file /home/certs/sometest.cer
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/mimimetr.jar"]