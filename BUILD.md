# Building Issuetrax

## Prerequisites

- Android SDK (API level 34)
- Java 17 or higher
- Android Studio (recommended) or command line tools

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