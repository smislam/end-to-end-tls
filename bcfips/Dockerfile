FROM maven:3-jdk-8-alpine AS BUILDER
WORKDIR /app
COPY . /app
RUN mvn clean package install 

FROM amazoncorretto:8-alpine-jre
WORKDIR /
COPY --from=BUILDER /app/target/*.jar app.jar

ENV PORT 9999
EXPOSE ${PORT}
CMD ["java","-jar","app.jar","-Dserver.port=${PORT}"]