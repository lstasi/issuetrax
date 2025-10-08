# Copilot Instructions for Issuetrax

## Project Overview

Issuetrax is a Kotlin Android application for focused GitHub issue management and Pull Request reviews with gesture-based navigation. The app follows a simplified single-repository workflow and emphasizes mobile-optimized PR review experiences.

## Development Context

- **Current Phase**: Definition/Design phase transitioning to implementation
- **Target Platform**: Android Native (Kotlin)
- **Architecture**: Clean Architecture + MVVM
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: API 34 (Android 14) - **No backward compatibility**
- **Target SDK**: API 34 (Android 14)
- **Philosophy**: Minimal dependencies, simple UI, no animations unless essential

## Key Technologies & Dependencies (Minimal Essential Only)

- **Language**: Kotlin with coroutines and flows
- **UI**: Jetpack Compose with Material Design 3 (minimal components)
- **Navigation**: Jetpack Navigation Compose
- **DI**: Hilt (Dagger-Hilt)
- **Networking**: Retrofit 2 + OkHttp 3
- **Serialization**: Kotlinx Serialization
- **Local Storage**: DataStore (preferences only)
- **Authentication**: GitHub Personal Access Token (PAT) - simplified
- **Security**: Security-crypto for encrypted token storage
- **Testing**: JUnit, Espresso, Compose Testing (basic only)

### Explicitly Removed (Not Used)
- ❌ Room Database (no local caching)
- ❌ WorkManager (no background sync)
- ❌ Coil Image Loading (no images)
- ❌ Material Icons Extended (basic icons only)
- ❌ Desugaring (not needed with minSdk 34)

## Architecture Guidelines

### Clean Architecture Layers (Minimal)
1. **Presentation Layer**: Compose UI + ViewModels
2. **Domain Layer**: Use Cases + Entities (business logic)
3. **Data Layer**: Repositories + Data Sources (API only - no local DB)

### Key Patterns
- MVVM with ViewModels managing UI state
- Repository pattern for data access abstraction
- Use Cases for business logic encapsulation
- Dependency inversion with interfaces
- Unidirectional data flow with StateFlow/Flow

## Code Style & Standards

### Kotlin Standards
- Follow official Kotlin coding conventions
- Use `camelCase` for functions, properties, and variables
- Use `PascalCase` for classes and interfaces
- Prefer `val` over `var` when possible
- Use trailing commas in multi-line parameter lists

### Compose Guidelines
- Use `@Composable` functions for UI components
- Prefer `LazyColumn`/`LazyRow` for dynamic lists
- Use `remember` and `rememberSaveable` appropriately
- Follow Material Design 3 patterns
- Implement proper state hoisting

### Architecture Patterns
```kotlin
// ViewModels should expose StateFlow/Flow
class MyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
}

// Use Cases should be injected and handle business logic
class GetPullRequestsUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    suspend operator fun invoke(owner: String, repo: String): Flow<Result<List<PullRequest>>> {
        // Implementation
    }
}

// Repositories should abstract data sources
interface GitHubRepository {
    suspend fun getPullRequests(owner: String, repo: String): Flow<Result<List<PullRequest>>>
}
```

## Project Structure

```
app/src/main/java/com/issuetrax/app/
├── data/
│   ├── api/                 # GitHub API interfaces
│   ├── repository/          # Repository implementations
│   └── local/              # DataStore only (no Room database)
├── domain/
│   ├── entity/             # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/           # Business logic use cases
├── presentation/
│   ├── ui/
│   │   ├── auth/           # Authentication screens
│   │   ├── current_work/   # Current work screen
│   │   ├── pr_review/      # PR review screens (primary focus)
│   │   └── common/         # Shared UI components
│   ├── navigation/         # Navigation setup
│   └── theme/             # Material Design 3 theme
└── di/                    # Hilt modules
```

## Core Features & Implementation

### GitHub Integration
- Use GitHub REST API v3 for data operations
- Implement Personal Access Token (PAT) authentication (simplified - no OAuth)
- No local database caching (DataStore for preferences only)
- Handle rate limiting and network errors gracefully

### Gesture-Based Navigation
- Implement custom gesture detection for PR reviews:
  - Swipe left/right: Navigate between files
  - Swipe up/down: Navigate between code hunks
  - Double tap: Expand/collapse code sections
  - Long press: Quick comment mode

### PR Review Features
- Inline diff viewer optimized for mobile
- Syntax highlighting for multiple languages
- Comment threading and draft comments
- Review submission workflow
- Source code browser integration

## Testing Guidelines

### Unit Tests
- Test ViewModels with coroutine testing utilities
- Mock dependencies using MockK
- Test Use Cases in isolation
- Achieve 80%+ code coverage for business logic

### UI Tests
- Use Compose Testing for UI components
- Test navigation flows end-to-end
- Verify gesture recognition functionality
- Test offline scenarios

### Example Test Structure
```kotlin
@Test
fun `when getPullRequests is called, should return success with data`() = runTest {
    // Given
    val expected = listOf(mockPullRequest)
    coEvery { repository.getPullRequests(any(), any()) } returns flowOf(Result.success(expected))
    
    // When
    val result = useCase("owner", "repo").first()
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(expected, result.getOrNull())
}
```

## Development Workflow

### Branch Strategy
- Use feature branches: `feature/feature-name`
- Create PRs for all changes
- Require code review before merging
- Follow conventional commit messages

### Code Quality
- Run ktlint for code formatting
- Use detekt for static analysis
- Ensure all tests pass before merging
- Document public APIs with KDoc

## Documentation

### Key Documents
- `PROJECT_DEFINITION.md`: Overall project requirements and roadmap
- `ARCHITECTURE.md`: Technical architecture details
- `UI_UX_DESIGN.md`: Design specifications and gesture system
- `PROJECT_SETUP.md`: Development environment setup

### Code Documentation
- Document complex business logic
- Add KDoc for public APIs
- Include usage examples for custom components
- Maintain README for setup instructions

## Performance Considerations

- Use `LazyColumn` for large lists
- No image loading library needed (minimal UI, no images)
- Optimize Compose recompositions
- Handle large PR diffs efficiently
- Implement proper memory management

## Security Guidelines

- Store sensitive data in EncryptedSharedPreferences
- Validate all API responses
- Implement proper PAT (Personal Access Token) management
- Use certificate pinning for API calls
- Follow Android security best practices

## Accessibility

- Add content descriptions for UI elements
- Support TalkBack navigation
- Ensure proper touch targets (48dp minimum)
- Test with accessibility services enabled
- Follow Material Design accessibility guidelines

## Common Patterns

When implementing new features:
1. Start with domain entities and use cases
2. Create repository interfaces in domain layer
3. Implement repositories in data layer
4. Create ViewModels in presentation layer
5. Build Compose UI components
6. Add navigation integration
7. Write comprehensive tests
8. Update documentation

Remember: This project emphasizes gesture-based mobile PR reviews with a clean, focused workflow. Always consider the mobile-first experience and optimize for single-handed operation where possible.