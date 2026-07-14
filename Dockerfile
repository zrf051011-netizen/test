# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-8 AS builder

WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B clean package

FROM tomcat:9.0-jdk8-temurin

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    TZ=Asia/Shanghai

RUN rm -rf /usr/local/tomcat/webapps/* \
    && mkdir -p /usr/local/tomcat/webapps/dormitory-system

COPY --from=builder /workspace/target/dormitory-system.war /tmp/dormitory-system.war

RUN cd /usr/local/tomcat/webapps/dormitory-system \
    && jar -xf /tmp/dormitory-system.war \
    && sed -i 's#jdbc:mysql://localhost:3306/#jdbc:mysql://mysql:3306/#' WEB-INF/classes/c3p0-config.xml \
    && rm /tmp/dormitory-system.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
