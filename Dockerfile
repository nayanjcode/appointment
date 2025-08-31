# Use Eclipse Temurin OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Copy the source code
COPY src /app/src

# Make Gradle wrapper executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Copy the built jar file to the container
RUN cp build/libs/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
