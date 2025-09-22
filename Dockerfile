# Stage 1: Build the application using a Gradle image
FROM gradle:8.7-jdk17 AS build

WORKDIR /home/gradle/src

# --- Caching Optimization ---
# 1. Copy only the files needed to resolve dependencies
COPY build.gradle.kts settings.gradle.kts ./

# 2. Download dependencies. Docker will cache this layer.
RUN gradle dependencies --no-daemon

# --- End Optimization ---

# 3. Copy the rest of the source code
COPY src ./src

# 4. Run the build, but EXCLUDE the test task to prevent connection errors
RUN gradle build --no-daemon -x test


# Stage 2: Create the final, lightweight production image
FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

# Copy only the built .jar file from the 'build' stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

