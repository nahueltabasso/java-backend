ARG MSVC_NAME=auth-service
FROM bellsoft/liberica-openjdk-alpine-musl:18.0.1.1 as builder
ARG MSVC_NAME
WORKDIR /app/$MSVC_NAME

COPY ./pom.xml /app
COPY ./$MSVC_NAME/.mvn ./.mvn
COPY ./$MSVC_NAME/mvnw .
COPY ./$MSVC_NAME/pom.xml .
COPY ./custom-dependencies/common-annotations-1.0-SNAPSHOT.jar /app/libs/
COPY ./custom-dependencies/nrt-common-microservice-1.0.0-SNAPSHOT.jar /app/libs

RUN ./mvnw install:install-file -Dfile=/app/libs/common-annotations-1.0-SNAPSHOT.jar -DgroupId=org.example -DartifactId=common-annotations -Dversion=1.0-SNAPSHOT -Dpackaging=jar
RUN ./mvnw install:install-file -Dfile=/app/libs/nrt-common-microservice-1.0.0-SNAPSHOT.jar -DgroupId=nrt.common.microservice -DartifactId=nrt-common-microservice -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar

RUN ./mvnw dependency:resolve
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

COPY ./$MSVC_NAME/src ./src

RUN ./mvnw clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine-musl:18.0.1.1
ARG MSVC_NAME
WORKDIR /app

COPY --from=builder /root/.m2/repository/ /root/.m2/repository/
COPY --from=builder /app/$MSVC_NAME/target/auth-service-0.0.1-SNAPSHOT.jar .

ENV PORT 8091
EXPOSE $PORT

CMD ["java", "-jar", "auth-service-0.0.1-SNAPSHOT.jar"]