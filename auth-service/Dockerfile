FROM bellsoft/liberica-openjdk-alpine-musl:18.0.1.1 as builder

WORKDIR /app/apigateway-service

COPY ./pom.xml /app
COPY ./apigateway-service/.mvn ./.mvn
COPY ./apigateway-service/mvnw .
COPY ./apigateway-service/pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

COPY ./apigateway-service/src ./src

RUN ./mvnw clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine-musl:18.0.1.1

WORKDIR /app

COPY --from=builder /app/apigateway-service/target/apigateway-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8090

CMD ["java", "-jar", "apigateway-service-0.0.1-SNAPSHOT.jar"]
