# Building Issuetrax

## Prerequisites

- Android SDK (API level 34)
- Java 17 or higher
- Android Studio (recommended) or command line tools

**Alternative**: Use Docker for containerized builds (see [Docker Build Guide](DOCKER.md))

## Building from Command Line

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Running Tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Building with Docker

For consistent builds across environments, use Docker:

```bash
# Build and serve APKs via web interface
docker-compose up --build

# Access APKs at http://localhost:8080
```

See [DOCKER.md](DOCKER.md) for detailed Docker build instructions.

## CI/CD

The project includes GitHub Actions workflows for:
- **Continuous Integration**: Runs tests and builds on every PR
- **Release**: Automatically builds and publishes releases when tags are pushed
- **Dependency Updates**: Weekly dependency update checks
- **Security Scanning**: Automated vulnerability scanning

## Project Structure

The project follows Clean Architecture principles:

```
app/src/main/java/com/issuetrax/app/
├── data/                    # Data layer (API, Repository implementations)
├── domain/                  # Domain layer (Entities, Use Cases, Repository interfaces)
├── presentation/            # Presentation layer (UI, ViewModels)
│   ├── ui/
│   │   ├── auth/           # Authentication screens
│   │   ├── current_work/   # Current work screen
│   │   ├── pr_review/      # PR review screens
│   │   └── common/         # Shared UI components and theme
│   └── navigation/         # Navigation setup
├── di/                      # Dependency Injection modules
└── IssuetraxApplication.kt  # Application class
```

## Key Technologies

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Hilt**: Dependency injection
- **Retrofit**: Network client
- **Room**: Local database
- **Navigation Compose**: Navigation framework
- **Material Design 3**: UI design system

## Getting Started

1. Clone the repository
2. Open in Android Studio or use command line
3. Sync project with Gradle files
4. Run on device or emulator

For detailed setup instructions, see [doc/PROJECT_SETUP.md](doc/PROJECT_SETUP.md).