# Issuetrax - Technical Architecture

## Overview

This document details the technical architecture for the Issuetrax Kotlin mobile application, focusing on the implementation details that support the gesture-based PR review system and GitHub integration.

**For detailed class-by-class documentation, see [CLASS_ARCHITECTURE.md](CLASS_ARCHITECTURE.md)** - Complete reference of all classes, their responsibilities, and relationships.

## Architecture Patterns

### Clean Architecture Implementation

```
┌─────────────────────────────────────────┐
│              Presentation Layer          │
│  ┌─────────────┐  ┌─────────────────────┐│
│  │   Compose   │  │    ViewModels       ││
│  │     UI      │  │   (State Mgmt)      ││
│  └─────────────┘  └─────────────────────┘│
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│               Domain Layer               │
│  ┌─────────────┐  ┌─────────────────────┐│
│  │  Use Cases  │  │     Entities        ││
│  │ (Business   │  │   (Domain Models)   ││
│  │   Logic)    │  │                     ││
│  └─────────────┘  └─────────────────────┘│
└─────────────┬───────────────────────────┘
              │
┌─────────────▼───────────────────────────┐
│                Data Layer                │
│  ┌─────────────┐  ┌─────────────────────┐│
│  │ Repositories│  │   Data Sources      ││
│  │(Abstraction)│  │  (API, Cache, DB)   ││
│  └─────────────┘  └─────────────────────┘│
└─────────────────────────────────────────┘
```

## Core Components

### 1. Gesture Recognition System

```kotlin
// Custom gesture detector for PR review navigation
class PRReviewGestureDetector(
    private val onSwipeRight: () -> Unit,    // Next file
    private val onSwipeLeft: () -> Unit,     // Previous file
    private val onSwipeUp: () -> Unit,       // Next hunk
    private val onSwipeDown: () -> Unit,     // Previous hunk
    private val onDoubleTap: () -> Unit,     // Toggle inline view
    private val onLongPress: (Offset) -> Unit // Comment mode
) {
    // Implementation details for gesture recognition
    // Minimum swipe distance, velocity thresholds, etc.
}
```

### 2. PR Review State Management

```kotlin
data class PRReviewUiState(
    val pullRequest: PullRequest?,
    val files: List<PRFile> = emptyList(),
    val currentFileIndex: Int = 0,
    val currentHunkIndex: Int = 0,
    val viewMode: DiffViewMode = DiffViewMode.INLINE,
    val comments: Map<String, List<Comment>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class DiffViewMode {
    INLINE,
    EXPANDED
}
```

### 3. GitHub API Integration

```kotlin
interface GitHubApiService {
    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "open"
    ): Response<List<PullRequest>>
    
    @GET("repos/{owner}/{repo}/pulls/{pull_number}/files")
    suspend fun getPullRequestFiles(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ): Response<List<PRFile>>
    
    @POST("repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    suspend fun submitReview(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int,
        @Body review: ReviewSubmission
    ): Response<Review>
}
```

## Data Models

### Core Entities

```kotlin
@Serializable
data class PullRequest(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val user: User,
    val head: Branch,
    val base: Branch,
    val reviewComments: Int,
    val comments: Int,
    val commits: Int,
    val additions: Int,
    val deletions: Int,
    val changedFiles: Int,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class PRFile(
    val filename: String,
    val status: String, // "added", "modified", "removed"
    val additions: Int,
    val deletions: Int,
    val changes: Int,
    val patch: String?,
    val contentsUrl: String,
    val rawUrl: String
)

@Serializable
data class Comment(
    val id: Long,
    val body: String,
    val user: User,
    val createdAt: String,
    val updatedAt: String,
    val path: String?,
    val position: Int?,
    val line: Int?,
    val side: String? // "LEFT" or "RIGHT"
)
```

## Screen Implementations

### PR Review Screen Architecture

```kotlin
@Composable
fun PRReviewScreen(
    prNumber: Int,
    viewModel: PRReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gestureDetector = rememberPRReviewGestureDetector(
        onSwipeRight = { viewModel.nextFile() },
        onSwipeLeft = { viewModel.previousFile() },
        onSwipeUp = { viewModel.nextHunk() },
        onSwipeDown = { viewModel.previousHunk() },
        onDoubleTap = { viewModel.toggleViewMode() },
        onLongPress = { offset -> viewModel.startComment(offset) }
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectGestures(gestureDetector)
            }
    ) {
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorMessage(uiState.error)
            else -> PRReviewContent(uiState, viewModel)
        }
    }
}
```

### Swipe Navigation Implementation

```kotlin
@Composable
fun rememberPRReviewGestureDetector(
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onDoubleTap: () -> Unit,
    onLongPress: (Offset) -> Unit
): PRReviewGestureDetector {
    return remember {
        PRReviewGestureDetector(
            onSwipeRight = onSwipeRight,
            onSwipeLeft = onSwipeLeft,
            onSwipeUp = onSwipeUp,
            onSwipeDown = onSwipeDown,
            onDoubleTap = onDoubleTap,
            onLongPress = onLongPress
        )
    }
}

// Custom gesture detection logic
suspend fun PointerInputScope.detectGestures(
    detector: PRReviewGestureDetector
) {
    detectDragGestures(
        onDragEnd = {
            detector.onDragEnd()
        }
    ) { change, dragAmount ->
        detector.onDrag(change, dragAmount)
    }
}
```

## Caching Strategy

### Multi-Level Caching

```kotlin
class GitHubRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val localCache: LocalCache,
    private val memoryCache: MemoryCache
) {
    suspend fun getPullRequests(owner: String, repo: String): Flow<List<PullRequest>> {
        return flow {
            // 1. Emit cached data immediately
            memoryCache.getPullRequests(owner, repo)?.let { emit(it) }
            
            // 2. Try local database
            localCache.getPullRequests(owner, repo).let { cached ->
                if (cached.isNotEmpty()) emit(cached)
            }
            
            // 3. Fetch from network
            try {
                val fresh = apiService.getPullRequests(owner, repo).body()
                fresh?.let {
                    memoryCache.storePullRequests(owner, repo, it)
                    localCache.storePullRequests(owner, repo, it)
                    emit(it)
                }
            } catch (e: Exception) {
                // Handle network error
            }
        }
    }
}
```

## Performance Optimizations

### 1. Lazy Loading for Large PRs

```kotlin
@Composable
fun FileListLazy(
    files: List<PRFile>,
    onFileSelected: (Int) -> Unit
) {
    LazyColumn {
        itemsIndexed(files) { index, file ->
            FileItem(
                file = file,
                onClick = { onFileSelected(index) }
            )
        }
    }
}
```

### 2. Diff Rendering Optimization

```kotlin
@Composable
fun DiffView(
    file: PRFile,
    viewMode: DiffViewMode
) {
    val diffLines = remember(file.patch) {
        parseDiffPatch(file.patch ?: "")
    }
    
    LazyColumn {
        items(diffLines) { line ->
            DiffLine(
                line = line,
                viewMode = viewMode
            )
        }
    }
}

data class DiffLine(
    val content: String,
    val type: DiffLineType, // ADDED, REMOVED, CONTEXT
    val lineNumber: Int?,
    val oldLineNumber: Int?
)
```

## Security Implementation

### 1. OAuth Token Management

```kotlin
class TokenManager @Inject constructor(
    private val encryptedPrefs: EncryptedSharedPreferences,
    private val keystore: AndroidKeystore
) {
    suspend fun storeTokens(accessToken: String, refreshToken: String?) {
        val encryptedAccessToken = keystore.encrypt(accessToken)
        val encryptedRefreshToken = refreshToken?.let { keystore.encrypt(it) }
        
        encryptedPrefs.edit {
            putString(KEY_ACCESS_TOKEN, encryptedAccessToken)
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, encryptedRefreshToken) }
        }
    }
    
    suspend fun getAccessToken(): String? {
        val encrypted = encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)
        return encrypted?.let { keystore.decrypt(it) }
    }
}
```

### 2. Network Security

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .certificatePinner(
                CertificatePinner.Builder()
                    .add("api.github.com", "sha256/...")
                    .build()
            )
            .addInterceptor(AuthInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) 
                    HttpLoggingInterceptor.Level.BODY 
                else 
                    HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }
}
```

## Testing Architecture

### 1. ViewModel Testing

```kotlin
@Test
fun `swipe right navigates to next file`() = runTest {
    val viewModel = PRReviewViewModel(mockRepository)
    viewModel.loadPullRequest(123)
    
    viewModel.nextFile()
    
    val uiState = viewModel.uiState.value
    assertEquals(1, uiState.currentFileIndex)
}
```

### 2. Gesture Testing

```kotlin
@Test
fun `swipe gesture triggers correct action`() {
    composeTestRule.setContent {
        PRReviewScreen(prNumber = 123)
    }
    
    composeTestRule.onRoot()
        .performGesture {
            swipeRight()
        }
    
    // Verify navigation occurred
    composeTestRule.onNodeWithText("Next file loaded")
        .assertIsDisplayed()
}
```

## Build Configuration

### Gradle Setup

```kotlin
// build.gradle.kts (app level)
// MINIMAL DEPENDENCIES CONFIGURATION - Android 14+ Only
android {
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.issuetrax.app"
        minSdk = 34  // Android 14+ only - no backward compatibility
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
}

dependencies {
    // Compose BOM - minimal UI components only
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Activity & Lifecycle
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")
    
    // Hilt - Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Networking - GitHub API only
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // DataStore - Simple preferences storage
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Security - Encrypted token storage
    implementation("androidx.security:security-crypto:1.1.0")
    
    // Custom Tabs - OAuth flow
    implementation("androidx.browser:browser:1.6.0")
    
    // Material Components - Theme support
    implementation("com.google.android.material:material:1.10.0")
}
```

**Note: Removed Dependencies for Minimal App**
- ❌ Room Database - No local caching required
- ❌ WorkManager - No background sync needed
- ❌ Coil Image Loading - No images to load
- ❌ Material Icons Extended - Use basic icons only
- ❌ Core Library Desugaring - Not needed with minSdk 34

This technical architecture provides the foundation for implementing the gesture-based PR review system while maintaining clean, testable, and performant code. The modular approach allows for iterative development and easy maintenance as the application evolves.