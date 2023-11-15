# Use the official gradle image to create a build artifact.
# https://hub.docker.com/_/gradle
FROM gradle:7.6-jdk11 as builder

# Copy local code to the container image.
COPY build.gradle .
COPY src ./src
# Build a release artifact.
RUN GRADLE_OPTS="-XX:MaxMetaspaceSize=4096m -XX:+HeapDumpOnOutOfMemoryError -Xmx512m -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en -Duser.variant"  gradle clean build -x test --no-daemon

# Use the Official OpenJDK image for a lean production stage of our multi-stage build.
# https://hub.docker.com/_/openjdk
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM azul/zulu-openjdk:11.0.21
# Copy the jar to the production image from the builder stage.
COPY --from=builder /home/gradle/build/libs/gradle-all.jar /spotfire-solver.jar

# Run the web service on container startup.
CMD [ "java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "/spotfire-solver.jar" ]