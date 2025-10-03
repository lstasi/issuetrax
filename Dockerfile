# Multi-stage Dockerfile for building Issuetrax Android app
# This allows building the APK inside a container for consistent builds

# Build stage
FROM openjdk:18-jdk-slim as builder

# Java 17 is used for consistency with Android development requirements
# Android Gradle Plugin 8.1.4 and Kotlin 1.9.20 officially support Java 17

# Install required dependencies
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Download and install Android SDK
RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    cd $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip commandlinetools-linux-9477386_latest.zip && \
    mv cmdline-tools latest && \
    rm commandlinetools-linux-9477386_latest.zip

# Accept Android SDK licenses
RUN yes | sdkmanager --licenses

# Install required Android SDK components
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Set working directory
WORKDIR /app

# Copy Gradle wrapper files
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/

# Copy project files
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY app/ app/

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew assembleDebug assembleRelease --no-daemon

# Runtime stage (for serving built APKs)
FROM nginx:alpine as runtime

# Copy built APKs to nginx directory
COPY --from=builder /app/app/build/outputs/apk/ /usr/share/nginx/html/apk/

# Create a simple index.html for downloading APKs
RUN echo '<!DOCTYPE html>' > /usr/share/nginx/html/index.html && \
    echo '<html><head><title>Issuetrax APK Downloads</title></head><body>' >> /usr/share/nginx/html/index.html && \
    echo '<h1>Issuetrax APK Downloads</h1>' >> /usr/share/nginx/html/index.html && \
    echo '<ul>' >> /usr/share/nginx/html/index.html && \
    echo '<li><a href="/apk/debug/">Debug APK</a></li>' >> /usr/share/nginx/html/index.html && \
    echo '<li><a href="/apk/release/">Release APK</a></li>' >> /usr/share/nginx/html/index.html && \
    echo '</ul></body></html>' >> /usr/share/nginx/html/index.html

# Enable directory listing for APK directories
RUN echo 'location /apk/ { autoindex on; }' > /etc/nginx/conf.d/apk-listing.conf

EXPOSE 80

# Labels for metadata
LABEL org.opencontainers.image.title="Issuetrax Android Builder"
LABEL org.opencontainers.image.description="Docker container for building Issuetrax Android APKs"
LABEL org.opencontainers.image.source="https://github.com/lstasi/Issuetrax"