FROM eclipse-temurin:17-jdk-focal as builder

COPY ./ /home/app

WORKDIR /home/app

RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash - \
    && apt-get install -y nodejs

RUN npm install && npm run build

RUN ./mvnw package -Dmaven.test.skip

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

FROM eclipse-temurin:17-jre-focal as runner

WORKDIR /app

COPY --from=builder /home/app/app.jar app.jar

COPY start.sh start.sh

EXPOSE 8080

ENTRYPOINT [ "sh", "start.sh"]