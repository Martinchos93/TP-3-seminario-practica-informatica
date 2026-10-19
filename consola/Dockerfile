FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /fuente
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=compilacion /fuente/target/tribu-consola.jar .
ENV LANG=C.UTF-8
ENTRYPOINT ["java", "-Dstdout.encoding=UTF-8", "-jar", "tribu-consola.jar"]
