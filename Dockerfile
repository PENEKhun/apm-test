FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy only the files needed for dependency resolution first (for better layer caching)
COPY build.gradle settings.gradle* gradle.properties* ./
COPY gradle ./gradle
COPY gradlew gradlew.bat ./
RUN chmod +x ./gradlew

# Download dependencies (this layer will be cached unless the build files change)
RUN ./gradlew dependencies --no-daemon

# Now copy the source code (which changes more frequently)
COPY src ./src

# Install Sentry CLI
RUN curl -sL https://sentry.io/get-cli/ | bash

# Build the application with Sentry source bundling disabled
ENV SENTRY_DISABLE_SOURCE_BUNDLE=true
RUN ./gradlew build --no-daemon

FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Download the Sentry OpenTelemetry agent
RUN curl -sL https://repo1.maven.org/maven2/io/sentry/sentry-opentelemetry-agent/8.13.3/sentry-opentelemetry-agent-8.13.3.jar -o sentry-opentelemetry-agent-8.13.3.jar

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV SENTRY_AUTO_INIT=false
ENV JAVA_TOOL_OPTIONS="-javaagent:sentry-opentelemetry-agent-8.13.3.jar"

ENTRYPOINT ["java", "-jar", "app.jar"]
