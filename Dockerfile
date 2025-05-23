# syntax=docker/dockerfile:1.3
FROM maven:3.6.3-openjdk-8-slim as build
WORKDIR /workspace/app
COPY pom.xml .
# COPY .git .git


# download the dependency if needed or if the pom file is changed
#RUN ./mvnw dependency:go-offline -B

COPY src src
VOLUME /root/.m2/
RUN ls
RUN mvn install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar )

FROM clinisys/base-core:8-jre-alpine-curl-tzdata
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENV PROFILE=prod
HEALTHCHECK --start-period=3m CMD curl -f http://localhost:8090/health || exit 1

CMD java $JAVA_OPTS -Dspring.profiles.active=$PROFILE -cp app:app/lib/* com.example.backend
