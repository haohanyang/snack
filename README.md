# Snack

Snack("chat" in Swedish) is a chat application built using Spring Boot and Spring Cloud's integration with AWS. The application enables
real-time messaging and showcases the integration of Spring Cloud services with AWS. It allows users to communicate with
each other in real-time, join chat rooms, and participate in separate discussions. The app serves as a foundation for
more sophisticated chat applications and demonstrates the use of various Spring Cloud components on the AWS platform.

## Demo
![screenshot](src/main/resources/static/screenshot.png)
A frontend-only demo is served on [Netlify](https://snack-demo.netlify.app/)

## Features
**Real-time messaging**: The app uses Spring's WebSocket messaging on STOMP protocol to enable real-time messaging. On the client-side, the app uses [StompJs](https://stomp-js.github.io/) and [SockJs](https://github.com/sockjs/sockjs-client) to connect to the WebSocket server.

**User authentication and authorization**: Users must create an account and log in to participate in chat conversations.
The app uses Spring security's OAuth2 support to authenticate with AWS Cognito for identity and access management.

**Multimedia support**: Users can send and receive images in the chat messages. The service is enabled by Spring Cloud's
S3 integration and AWS CloudFront for fast content delivering.

**RTK Query** The front-end largely uses [RTK Query](https://redux-toolkit.js.org/rtk-query/overview) for simplified
data fetching and caching.

## Deployment
Build the docker image 
```
docker build -t snack-server:v1 .
```
Before running the container, encrypt the secrets in the properties file using Jasypt
```bash
# Encrypt the properties file
./mvnw jasypt:encrypt -Djasypt.plugin.path="file:/path/to/properties" -Djasypt.encryptor.password="password"

# Run the container
docker run -it --name snack-server-container --mount type=bind,source=/path/to/properties,target=/config/application.properties -p 8080:8080 -e spring_profiles_active=prod snack-server:v1 --spring.config.location=/config/application.properties --jasypt.encryptor.password="password"
```