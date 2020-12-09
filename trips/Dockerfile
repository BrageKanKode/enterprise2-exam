FROM adoptopenjdk/openjdk11:alpine

COPY target/trips.jar .

CMD java -Dspring.profiles.active=FakeData -jar trips.jar
