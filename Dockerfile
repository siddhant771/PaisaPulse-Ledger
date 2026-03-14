# Use an official Android SDK image
FROM mingchen/android-sdk:latest

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Run unit tests
RUN ./gradlew test