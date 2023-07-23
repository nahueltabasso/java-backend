ARG MSVC_NAME=apigateway-service
FROM bellsoft/liberica-openjdk-alpine-musl:18.0.1.1 as builder
ARG MSVC_NAME
WORKDIR /app/$MSVC_NAME

COPY ./pom.xml /app
COPY ./$MSVC_NAME/.mvn ./.mvn
COPY ./$MSVC_NAME/mvnw .
COPY ./$MSVC_NAME/pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

COPY ./$MSVC_NAME/src ./src

RUN ./mvnw clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine-musl:18.0.1.1
ARG MSVC_NAME
WORKDIR /app

COPY --from=builder /app/$MSVC_NAME/target/apigateway-service-0.0.1-SNAPSHOT.jar .
ENV PORT 8090

EXPOSE $PORT

CMD ["java", "-jar", "apigateway-service-0.0.1-SNAPSHOT.jar"]
