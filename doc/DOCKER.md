# Docker Build Guide for Issuetrax

This document explains how to build the Issuetrax Android application using Docker containers for consistent, reproducible builds across different environments.

## Quick Start

### Build and Serve APKs (Recommended)

```bash
# Build the container and start web server with APK downloads
docker-compose up --build

# Access APKs at http://localhost:8080
# - Debug APK: http://localhost:8080/apk/debug/
# - Release APK: http://localhost:8080/apk/release/
```

### Build APK Only

```bash
# Build debug APK only
docker build -f Dockerfile.build -t issuetrax-builder .
docker run --rm -v $(pwd)/build/docker-output:/app/app/build/outputs/apk issuetrax-builder

# Build release APK
docker run --rm -v $(pwd)/build/docker-output:/app/app/build/outputs/apk issuetrax-builder ./gradlew assembleRelease --no-daemon
```

## Docker Files Overview

### `Dockerfile`
Multi-stage build that creates both the APK and serves it via nginx web server:
- **Stage 1 (builder)**: Builds the Android APK using OpenJDK 17 and Android SDK
- **Stage 2 (runtime)**: Serves the built APKs via nginx for easy download

### `Dockerfile.build`
Simple single-stage build for APK generation only:
- Builds the APK without web server overhead
- Suitable for CI/CD pipelines
- Smaller final container size

### `docker-compose.yml`
Orchestrates services:
- **issuetrax-builder**: Main service that builds and serves APKs
- **issuetrax-dev**: Development container (profile: dev)

## Usage Examples

### 1. Build and Access APKs via Web Interface

```bash
# Start the build and web server
docker-compose up --build

# Open browser to http://localhost:8080
# Download APKs from the web interface
```

### 2. Development Environment

```bash
# Start development container
docker-compose --profile dev up -d issuetrax-dev

# Execute commands in the container
docker-compose exec issuetrax-dev ./gradlew assembleDebug
docker-compose exec issuetrax-dev ./gradlew test
docker-compose exec issuetrax-dev bash
```

### 3. CI/CD Pipeline Usage

```bash
# Build in CI environment
docker build -f Dockerfile.build -t issuetrax-builder .
docker run --rm -v $(pwd)/output:/app/app/build/outputs/apk issuetrax-builder

# APKs will be available in ./output directory
```

### 4. Custom Build Commands

```bash
# Build specific variant
docker run --rm -v $(pwd)/output:/app/app/build/outputs/apk issuetrax-builder ./gradlew assembleRelease

# Run tests
docker run --rm issuetrax-builder ./gradlew test

# Run lint
docker run --rm issuetrax-builder ./gradlew lint
```

## Environment Requirements

- **Docker**: 20.10 or later
- **Docker Compose**: 2.0 or later (for compose commands)
- **Host OS**: Linux, macOS, or Windows with WSL2
- **Java**: OpenJDK 17 (LTS version recommended for Android development)

## Volume Mounts

### APK Output
- Container path: `/app/app/build/outputs/apk`
- Mount to host for accessing built APKs

### Gradle Cache (Optional)
- Container path: `/root/.gradle`
- Mount to speed up subsequent builds

### Source Code (Development)
- Container path: `/app`
- Mount for live development

## Environment Variables

The containers support these environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `ANDROID_HOME` | Android SDK location | `/opt/android-sdk` |
| `JAVA_HOME` | Java installation path | Auto-detected |
| `GRADLE_OPTS` | Gradle JVM options | `-Xmx2g` |

## Troubleshooting

### Build Fails with "SDK License Not Accepted"
The container automatically accepts licenses. If issues persist:
```bash
docker run --rm -it issuetrax-builder sdkmanager --licenses
```

### Out of Memory Errors
Increase Docker memory allocation or set gradle memory limits:
```bash
docker run --rm -e GRADLE_OPTS="-Xmx1g" issuetrax-builder ./gradlew assembleDebug
```

### Permission Issues (Linux/macOS)
Ensure proper permissions on output directories:
```bash
mkdir -p build/docker-output
chmod 755 build/docker-output
```

## Container Sizes

- **Full container (with nginx)**: ~2.5GB
- **Build-only container**: ~2.2GB
- **Android SDK**: ~1.8GB
- **Base JDK**: ~400MB

## Security Considerations

- Containers run with non-privileged users where possible
- No secrets or credentials are baked into images
- Use multi-stage builds to minimize attack surface
- Regular base image updates recommended