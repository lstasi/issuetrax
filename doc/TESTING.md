# Testing Documentation

This document describes the testing strategy, setup, and guidelines for the Issuetrax Android application.

## Testing Philosophy

We follow **Classic TDD (Test-Driven Development)** principles:

- **Test behavior, not implementation**: Focus on what the code does, not how it does it
- **Minimal mocking**: Mock only external dependencies (APIs, databases, etc.)
- **Real data where possible**: Use actual data classes instead of mocks for domain entities
- **Clear test structure**: Given-When-Then pattern for readability
- **Isolated tests**: Each test should be independent and not rely on others

## Test Structure

### Test Organization

```
app/src/test/
├── java/com/issuetrax/app/
│   ├── domain/
│   │   └── usecase/          # Use case tests (no mocks, only repository mocks)
│   ├── presentation/
│   │   └── ui/               # ViewModel tests (mock use cases & repositories)
│   └── data/
│       └── repository/       # Repository tests (mock API service)
└── resources/
    └── api_responses/        # Sample API response JSON files
```

### Test Types

#### 1. Domain Layer Tests (Use Cases)
- **What to test**: Business logic and orchestration
- **What to mock**: Only repository interfaces
- **Example**: `GetPullRequestsUseCaseTest`

```kotlin
@Test
fun `invoke should return success result with pull requests from repository`() = runTest {
    // Given
    val mockPRs = listOf(createTestPR())
    coEvery { repository.getPullRequests(any(), any(), any()) } 
        returns flowOf(Result.success(mockPRs))
    
    // When
    val result = useCase("owner", "repo").first()
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(mockPRs, result.getOrNull())
}
```

#### 2. Presentation Layer Tests (ViewModels)
- **What to test**: State management and UI logic
- **What to mock**: Use cases and repositories
- **Example**: `CurrentWorkViewModelTest`

```kotlin
@Test
fun `loadPullRequests should update state to loading then success`() = runTest {
    // Given
    coEvery { useCase(any(), any(), any()) } returns flowOf(Result.success(mockPRs))
    
    // When
    viewModel.loadPullRequests("owner", "repo")
    advanceUntilIdle()
    
    // Then
    assertFalse(viewModel.uiState.value.isLoadingPRs)
    assertEquals(2, viewModel.uiState.value.pullRequests.size)
}
```

#### 3. Data Layer Tests (Repositories)
- **What to test**: Data transformation and error handling
- **What to mock**: API service interfaces
- **Example**: `GitHubRepositoryImplTest` (to be implemented)

## Test Dependencies

```gradle
// Unit Testing
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("io.mockk:mockk:1.14.6")
testImplementation("androidx.arch.core:core-testing:2.2.0")

// Android Testing
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

## Running Tests

### Run All Unit Tests
```bash
./gradlew test
```

### Run Tests for Specific Module
```bash
./gradlew :app:test
```

### Run Tests for Specific Class
```bash
./gradlew :app:testDebugUnitTest --tests "*.GetPullRequestsUseCaseTest"
```

### Run with Coverage
```bash
./gradlew :app:testDebugUnitTestCoverage
```

### View Test Results
- HTML report: `app/build/reports/tests/testDebugUnitTest/index.html`
- XML results: `app/build/test-results/testDebugUnitTest/`

## Sample API Responses

Sample API responses are stored in `app/src/test/resources/api_responses/` for testing:

- `user.json` - GitHub user profile response
- `repositories.json` - List of user repositories
- `pull_requests.json` - List of pull requests
- `pull_request_files.json` - List of changed files in a PR

### Validating API Responses

Use the validation script to verify that real API responses match expected format:

```bash
# Setup environment
cp .env_sample .env
# Edit .env and add your GITHUB_TOKEN

# Run validation
./scripts/validate_api_responses.sh
```

The script will:
1. Fetch live data from GitHub API
2. Validate response structure
3. Save responses as `*_live.json` files
4. Compare with mock responses

## Writing New Tests

### Template for Use Case Tests

```kotlin
class MyUseCaseTest {
    private lateinit var repository: MyRepository
    private lateinit var useCase: MyUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        useCase = MyUseCase(repository)
    }
    
    @Test
    fun `test description in natural language`() = runTest {
        // Given
        coEvery { repository.someMethod() } returns flowOf(Result.success(data))
        
        // When
        val result = useCase().first()
        
        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.someMethod() }
    }
}
```

### Template for ViewModel Tests

```kotlin
@ExperimentalCoroutinesApi
class MyViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: MyUseCase
    private lateinit var viewModel: MyViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk()
        viewModel = MyViewModel(useCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test state change`() = runTest {
        // Given
        coEvery { useCase() } returns flowOf(Result.success(data))
        
        // When
        viewModel.doSomething()
        advanceUntilIdle()
        
        // Then
        assertEquals(expectedState, viewModel.uiState.value)
    }
}
```

## Test Coverage Goals

- **Domain Layer (Use Cases)**: 90%+ coverage
- **Presentation Layer (ViewModels)**: 80%+ coverage  
- **Data Layer (Repositories)**: 80%+ coverage

## Current Test Status

### ✅ Implemented

#### Domain Layer
- `GetPullRequestsUseCaseTest` - 5 tests, all passing

#### Presentation Layer
- `CurrentWorkViewModelTest` - 11 tests, all passing
- `PRReviewViewModelTest` - 19 tests, all passing
- `PRReviewIntegrationTest` - 13 integration tests, all passing

#### Component Tests
- `FileNavigationButtonsTest` - 6 tests, all passing
- `PRMetadataCardTest` - 3 tests, all passing

#### Data Layer
- `FileDiffDtoTest` - Data model tests, all passing

### 🚧 To Be Implemented
- `GetUserRepositoriesUseCaseTest`
- `AuthenticateUseCaseTest`
- `SubmitReviewUseCaseTest`
- `AuthViewModelTest`
- `RepositorySelectionViewModelTest`
- `GitHubRepositoryImplTest`
- `AuthRepositoryImplTest`

## Continuous Integration

Tests are automatically run on:
- Every pull request
- Every push to main branch
- Before release builds

## Troubleshooting

### Common Issues

**Issue**: Tests fail with "Suspension functions can only be called within coroutine body"
- **Solution**: Use `coEvery` instead of `every` for suspend functions
- **Solution**: Use `coVerify` instead of `verify` for suspend functions

**Issue**: ViewModel state not updating in tests
- **Solution**: Call `advanceUntilIdle()` after triggering state changes
- **Solution**: Ensure `StandardTestDispatcher()` is used and properly set

**Issue**: Flow not collecting in tests
- **Solution**: Use `.first()` to collect first emission
- **Solution**: Use `toList()` for multiple emissions

**Issue**: Kapt warnings about language version
- **Note**: These warnings are expected with Kotlin 2.0+, kapt falls back to 1.9

## Best Practices

1. **One assertion per test** (when possible) - Makes failures easier to diagnose
2. **Test names describe behavior** - Use backticks for natural language
3. **Use real data** - Create actual data class instances instead of mocks
4. **Keep tests fast** - Mock external dependencies, avoid Thread.sleep()
5. **Test edge cases** - Empty lists, null values, error conditions
6. **Clean up resources** - Use @After to reset dispatchers and clean state

## References

- [Android Testing Guide](https://developer.android.com/training/testing)
- [Kotlin Coroutines Testing](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/)
- [MockK Documentation](https://mockk.io/)
- [Classic TDD](http://www.growing-object-oriented-software.com/)

---

**Last Updated**: Phase 1 testing implementation
**Test Framework**: JUnit 4
**Mocking Library**: MockK
**Coroutines**: kotlinx-coroutines-test
