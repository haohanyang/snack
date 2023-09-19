FROM amazoncorretto:17-alpine-jdk as builder
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw package -Dmaven.test.skip

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


FROM amazoncorretto:17-alpine
VOLUME /config
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 8080

RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

ENTRYPOINT ["java", "-cp", "app:app/lib/*:app/classes", "snack.SnackApplication"]