FROM amazoncorretto:17-alpine

WORKDIR /app

COPY target/*.jar app.jar

COPY wait-for-postgres.sh /wait-for-postgres.sh
RUN chmod +x /wait-for-postgres.sh

RUN apk add --no-cache busybox-extras

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
