# Issuetrax - Class Architecture Documentation

## Overview

This document provides a detailed reference of all classes in the Issuetrax application, organized by architectural layer. The application follows Clean Architecture principles with three main layers: Presentation, Domain, and Data.

**Current Implementation Status**: Basic implementation with GitHub API integration and UI screens for authentication, repository selection, and PR review.

**Architecture Philosophy**: Keep it simple - no over-engineering. The application is designed for a focused single-repository PR review workflow with minimal dependencies.

## Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  UI Components (Compose) + ViewModels + Navigation      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                      Domain Layer                        │
│     Entities + Use Cases + Repository Interfaces        │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                       Data Layer                         │
│  Repository Implementations + API + DTOs + Mappers      │
└─────────────────────────────────────────────────────────┘
```

---

## 1. Application Layer

### `IssuetraxApplication`
**Package**: `com.issuetrax.app`  
**Type**: Application Class  
**Purpose**: Main application class with Hilt dependency injection setup

**Annotations**:
- `@HiltAndroidApp` - Enables Hilt dependency injection

**Extends**: `Application`

**Responsibilities**:
- Application lifecycle management
- Hilt dependency injection initialization

---

## 2. Presentation Layer

### 2.1 Main Activity

#### `MainActivity`
**Package**: `com.issuetrax.app.presentation`  
**Type**: Activity  
**Purpose**: Single activity container for Compose UI

**Annotations**:
- `@AndroidEntryPoint` - Enables Hilt injection

**Extends**: `ComponentActivity`

**Key Methods**:
- `onCreate(savedInstanceState: Bundle?)` - Sets up Compose UI with theme and navigation

**Responsibilities**:
- Hosting the Compose UI
- Setting up Material Design 3 theme
- Initializing navigation graph

---

### 2.2 Navigation

#### `Routes`
**Package**: `com.issuetrax.app.presentation.navigation`  
**Type**: Sealed Class  
**Purpose**: Define navigation routes for the application

**Routes**:
- `Auth` - Authentication screen route: `"auth"`
- `RepositorySelection` - Repository selection screen route: `"repository_selection"`
- `CurrentWork` - Current work screen route: `"current_work/{owner}/{repo}"`
  - `createRoute(owner: String, repo: String): String` - Creates parameterized route
- `PRReview` - PR review screen route: `"pr_review/{owner}/{repo}/{prNumber}"`
  - `createRoute(owner: String, repo: String, prNumber: Int): String` - Creates parameterized route

**Responsibilities**:
- Type-safe navigation route definitions
- Route parameter management

#### `NavGraph`
**Package**: `com.issuetrax.app.presentation.navigation`  
**Type**: Composable Function  
**Purpose**: Defines the navigation graph for the application

**Responsibilities**:
- Setting up NavHost with navigation routes
- Defining screen destinations
- Handling navigation between screens

---

### 2.3 Authentication Screen

#### `AuthViewModel`
**Package**: `com.issuetrax.app.presentation.ui.auth`  
**Type**: ViewModel  
**Purpose**: Manages authentication screen state and logic

**Annotations**:
- `@HiltViewModel` - Enables Hilt injection

**Dependencies**:
- `AuthenticateUseCase` - Handles authentication logic
- `AuthRepository` - Checks authentication state

**Properties**:
- `uiState: StateFlow<AuthUiState>` - Exposed UI state

**Methods**:
- `authenticate(authCode: String)` - Initiates authentication with token
- `clearError()` - Clears error state

**State Flow**: Reactive state management with StateFlow

#### `AuthUiState`
**Package**: `com.issuetrax.app.presentation.ui.auth`  
**Type**: Data Class  
**Purpose**: Represents authentication UI state

**Properties**:
- `isLoading: Boolean = false` - Loading indicator state
- `isAuthenticated: Boolean = false` - Authentication status
- `error: String? = null` - Error message

#### `AuthScreen`
**Package**: `com.issuetrax.app.presentation.ui.auth`  
**Type**: Composable Function  
**Purpose**: UI for GitHub Personal Access Token authentication

**Responsibilities**:
- Displaying authentication form
- Collecting user token input
- Showing authentication status and errors
- Navigation to repository selection on success

---

### 2.4 Repository Selection Screen

#### `RepositorySelectionViewModel`
**Package**: `com.issuetrax.app.presentation.ui.repository_selection`  
**Type**: ViewModel  
**Purpose**: Manages repository selection state and operations

**Annotations**:
- `@HiltViewModel` - Enables Hilt injection

**Dependencies**:
- `GetUserRepositoriesUseCase` - Fetches user repositories
- `RepositoryContextRepository` - Persists selected repository

**Properties**:
- `uiState: StateFlow<RepositorySelectionUiState>` - Exposed UI state

**Methods**:
- `loadRepositories()` - Fetches user repositories from GitHub
- `selectRepository(owner: String, repo: String)` - Saves selected repository
- `clearError()` - Clears error state

**Initialization**: Automatically loads repositories on creation

#### `RepositorySelectionUiState`
**Package**: `com.issuetrax.app.presentation.ui.repository_selection`  
**Type**: Data Class  
**Purpose**: Represents repository selection UI state

**Properties**:
- `isLoading: Boolean = false` - Loading indicator
- `repositories: List<Repository> = emptyList()` - Available repositories
- `selectedRepository: String? = null` - Currently selected repository
- `error: String? = null` - Error message

#### `RepositorySelectionScreen`
**Package**: `com.issuetrax.app.presentation.ui.repository_selection`  
**Type**: Composable Function  
**Purpose**: UI for selecting a repository to work with

**Responsibilities**:
- Displaying list of user repositories
- Handling repository selection
- Navigation to current work screen

---

### 2.5 Current Work Screen

#### `CurrentWorkViewModel`
**Package**: `com.issuetrax.app.presentation.ui.current_work`  
**Type**: ViewModel  
**Purpose**: Manages current work screen state (repositories and PRs)

**Annotations**:
- `@HiltViewModel` - Enables Hilt injection

**Dependencies**:
- `GetPullRequestsUseCase` - Fetches pull requests
- `GitHubRepository` - Access to GitHub data

**Properties**:
- `uiState: StateFlow<CurrentWorkUiState>` - Exposed UI state

**Methods**:
- `loadUserRepositories()` - Loads user's repositories
- `loadPullRequests(owner: String, repo: String)` - Loads PRs for repository
- `clearError()` - Clears error state

#### `CurrentWorkUiState`
**Package**: `com.issuetrax.app.presentation.ui.current_work`  
**Type**: Data Class  
**Purpose**: Represents current work UI state

**Properties**:
- `isLoading: Boolean = false` - Repository loading state
- `isLoadingPRs: Boolean = false` - PR loading state
- `repositories: List<Repository> = emptyList()` - Available repositories
- `pullRequests: List<PullRequest> = emptyList()` - Open pull requests
- `selectedRepository: String? = null` - Currently selected repository
- `error: String? = null` - Error message

#### `CurrentWorkScreen`
**Package**: `com.issuetrax.app.presentation.ui.current_work`  
**Type**: Composable Function  
**Purpose**: UI showing repositories and open pull requests

**Responsibilities**:
- Displaying repository context
- Listing open pull requests
- Navigation to PR review screen

---

### 2.6 PR Review Screen

#### `PRReviewViewModel`
**Package**: `com.issuetrax.app.presentation.ui.pr_review`  
**Type**: ViewModel  
**Purpose**: Manages PR review screen state and file navigation

**Annotations**:
- `@HiltViewModel` - Enables Hilt injection

**Dependencies**:
- `GitHubRepository` - Access to PR data
- `SubmitReviewUseCase` - Submits PR reviews

**Properties**:
- `uiState: StateFlow<PRReviewUiState>` - Exposed UI state

**Methods**:
- `loadPullRequest(owner: String, repo: String, number: Int)` - Loads PR details and files
- `navigateToNextFile()` - Moves to next file in PR
- `navigateToPreviousFile()` - Moves to previous file in PR
- `submitReview(owner: String, repo: String, prNumber: Int, body: String?, event: ReviewEvent)` - Submits review
- `clearError()` - Clears error state

#### `PRReviewUiState`
**Package**: `com.issuetrax.app.presentation.ui.pr_review`  
**Type**: Data Class  
**Purpose**: Represents PR review UI state

**Properties**:
- `isLoading: Boolean = false` - Loading state
- `isSubmittingReview: Boolean = false` - Review submission state
- `pullRequest: PullRequest? = null` - Current PR details
- `files: List<FileDiff> = emptyList()` - Changed files in PR
- `currentFileIndex: Int = -1` - Index of currently viewed file
- `reviewSubmitted: Boolean = false` - Review submission status
- `error: String? = null` - Error message

**Computed Properties**:
- `currentFile: FileDiff?` - Returns current file being viewed

#### `PRReviewScreen`
**Package**: `com.issuetrax.app.presentation.ui.pr_review`  
**Type**: Composable Function  
**Purpose**: UI for reviewing pull requests with file diffs

**Responsibilities**:
- Displaying PR metadata
- Showing file diffs with syntax highlighting
- Gesture-based navigation between files
- Review submission interface

---

### 2.7 Theme Components

#### `Color`
**Package**: `com.issuetrax.app.presentation.ui.common.theme`  
**Type**: Kotlin File  
**Purpose**: Material Design 3 color definitions

**Contents**:
- Color palette for light and dark themes
- Primary, secondary, tertiary color schemes

#### `Type`
**Package**: `com.issuetrax.app.presentation.ui.common.theme`  
**Type**: Kotlin File  
**Purpose**: Typography definitions

**Contents**:
- Material Design 3 typography scale
- Font family definitions

#### `Theme`
**Package**: `com.issuetrax.app.presentation.ui.common.theme`  
**Type**: Kotlin File  
**Purpose**: Application theme setup

**Key Function**:
- `IssuetraxTheme()` - Main theme composable with light/dark mode support

---

## 3. Domain Layer

### 3.1 Entities

#### `User`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Data Class  
**Purpose**: Represents a GitHub user

**Properties**:
- `id: Long` - User ID
- `login: String` - Username
- `name: String?` - Display name
- `email: String?` - Email address
- `avatarUrl: String` - Avatar URL
- `htmlUrl: String` - Profile URL
- `type: UserType = UserType.USER` - User type

#### `UserType`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Enum  
**Purpose**: Defines user types

**Values**:
- `USER` - Regular user
- `ORGANIZATION` - Organization account
- `BOT` - Bot account

---

#### `Repository`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Data Class  
**Purpose**: Represents a GitHub repository

**Properties**:
- `id: Long` - Repository ID
- `name: String` - Repository name
- `fullName: String` - Owner/repo format
- `owner: User` - Repository owner
- `description: String?` - Description
- `private: Boolean` - Privacy status
- `htmlUrl: String` - Web URL
- `cloneUrl: String` - HTTPS clone URL
- `sshUrl: String` - SSH clone URL
- `defaultBranch: String` - Default branch name
- `language: String?` - Primary language
- `stargazersCount: Int` - Star count
- `forksCount: Int` - Fork count
- `openIssuesCount: Int` - Open issues count
- `createdAt: LocalDateTime` - Creation timestamp
- `updatedAt: LocalDateTime` - Last update timestamp
- `pushedAt: LocalDateTime?` - Last push timestamp

---

#### `PullRequest`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Data Class  
**Purpose**: Represents a GitHub pull request

**Properties**:
- `id: Long` - PR ID
- `number: Int` - PR number
- `title: String` - PR title
- `body: String?` - PR description
- `state: PRState` - PR state
- `author: User` - PR author
- `createdAt: LocalDateTime` - Creation timestamp
- `updatedAt: LocalDateTime` - Last update timestamp
- `mergedAt: LocalDateTime?` - Merge timestamp
- `closedAt: LocalDateTime?` - Close timestamp
- `mergeable: Boolean?` - Merge status
- `merged: Boolean` - Merged flag
- `draft: Boolean` - Draft status
- `reviewDecision: ReviewDecision?` - Review decision
- `changedFiles: Int` - Number of changed files
- `additions: Int` - Lines added
- `deletions: Int` - Lines deleted
- `commits: Int` - Number of commits
- `headRef: String` - Source branch
- `baseRef: String` - Target branch
- `htmlUrl: String` - Web URL

#### `PRState`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Enum  
**Purpose**: PR state values

**Values**:
- `OPEN` - Open PR
- `CLOSED` - Closed PR
- `MERGED` - Merged PR

#### `ReviewDecision`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Enum  
**Purpose**: Review decision status

**Values**:
- `APPROVED` - PR approved
- `CHANGES_REQUESTED` - Changes requested
- `REVIEW_REQUIRED` - Review required

---

#### `FileDiff`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Data Class  
**Purpose**: Represents a file diff in a PR

**Properties**:
- `filename: String` - File path
- `status: FileStatus` - Change type
- `additions: Int` - Lines added
- `deletions: Int` - Lines deleted
- `changes: Int` - Total changes
- `patch: String?` - Unified diff patch
- `blobUrl: String?` - Blob URL
- `rawUrl: String?` - Raw file URL
- `previousFilename: String? = null` - Previous name if renamed

#### `FileStatus`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Enum  
**Purpose**: File change status

**Values**:
- `ADDED` - New file
- `MODIFIED` - Modified file
- `REMOVED` - Deleted file
- `RENAMED` - Renamed file
- `COPIED` - Copied file
- `CHANGED` - Changed file
- `UNCHANGED` - Unchanged file

#### `CodeHunk`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Data Class  
**Purpose**: Represents a hunk in a diff

**Properties**:
- `oldStart: Int` - Old file start line
- `oldCount: Int` - Lines in old file
- `newStart: Int` - New file start line
- `newCount: Int` - Lines in new file
- `lines: List<DiffLine>` - Diff lines

#### `DiffLine`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Data Class  
**Purpose**: Represents a single line in a diff

**Properties**:
- `type: LineType` - Line type
- `content: String` - Line content
- `oldLineNumber: Int?` - Line number in old file
- `newLineNumber: Int?` - Line number in new file

#### `LineType`
**Package**: `com.issuetrax.app.domain.entity`  
**Type**: Enum  
**Purpose**: Diff line types

**Values**:
- `CONTEXT` - Context line (unchanged)
- `ADDITION` - Added line
- `DELETION` - Deleted line
- `NO_NEWLINE` - No newline marker

---

### 3.2 Repository Interfaces

#### `GitHubRepository`
**Package**: `com.issuetrax.app.domain.repository`  
**Type**: Interface  
**Purpose**: Abstract GitHub data access operations

**Methods**:
- `suspend fun getCurrentUser(): Result<User>` - Get authenticated user
- `suspend fun getUserRepositories(): Flow<Result<List<Repository>>>` - Get user repos
- `suspend fun getPullRequests(owner: String, repo: String, state: String = "open"): Flow<Result<List<PullRequest>>>` - Get PRs
- `suspend fun getPullRequest(owner: String, repo: String, number: Int): Result<PullRequest>` - Get single PR
- `suspend fun getPullRequestFiles(owner: String, repo: String, number: Int): Result<List<FileDiff>>` - Get PR files
- `suspend fun createReview(owner: String, repo: String, number: Int, body: String?, event: String, comments: List<ReviewComment>): Result<Unit>` - Submit review

#### `ReviewComment`
**Package**: `com.issuetrax.app.domain.repository`  
**Type**: Data Class  
**Purpose**: Represents a review comment

**Properties**:
- `path: String` - File path
- `position: Int` - Line position
- `body: String` - Comment text

---

#### `AuthRepository`
**Package**: `com.issuetrax.app.domain.repository`  
**Type**: Interface  
**Purpose**: Abstract authentication operations

**Methods**:
- `suspend fun authenticate(authCode: String): Result<String>` - Authenticate with token
- `suspend fun saveAccessToken(token: String)` - Store access token
- `suspend fun getAccessToken(): String?` - Retrieve access token
- `suspend fun clearAccessToken()` - Clear stored token
- `fun isAuthenticated(): Flow<Boolean>` - Check authentication status
- `suspend fun refreshToken(): Result<String>` - Refresh access token

---

#### `RepositoryContextRepository`
**Package**: `com.issuetrax.app.domain.repository`  
**Type**: Interface  
**Purpose**: Manage selected repository context

**Methods**:
- `suspend fun saveSelectedRepository(owner: String, repo: String)` - Save selection
- `fun getSelectedRepository(): Flow<Pair<String, String>?>` - Get selected repo
- `suspend fun clearSelectedRepository()` - Clear selection

---

### 3.3 Use Cases

#### `AuthenticateUseCase`
**Package**: `com.issuetrax.app.domain.usecase`  
**Type**: Class  
**Purpose**: Handle authentication flow

**Dependencies**:
- `AuthRepository` - Authentication operations

**Method**:
- `suspend operator fun invoke(authCode: String): Result<Unit>` - Authenticate and store token

**Logic**:
1. Authenticate with GitHub
2. Store access token on success
3. Return result

---

#### `GetUserRepositoriesUseCase`
**Package**: `com.issuetrax.app.domain.usecase`  
**Type**: Class  
**Purpose**: Fetch user repositories

**Dependencies**:
- `GitHubRepository` - GitHub operations

**Method**:
- `suspend operator fun invoke(): Flow<Result<List<Repository>>>` - Get repositories

**Logic**: Delegates to repository

---

#### `GetPullRequestsUseCase`
**Package**: `com.issuetrax.app.domain.usecase`  
**Type**: Class  
**Purpose**: Fetch pull requests for a repository

**Dependencies**:
- `GitHubRepository` - GitHub operations

**Method**:
- `suspend operator fun invoke(owner: String, repo: String, state: String = "open"): Flow<Result<List<PullRequest>>>` - Get PRs

**Logic**: Delegates to repository with parameters

---

#### `SubmitReviewUseCase`
**Package**: `com.issuetrax.app.domain.usecase`  
**Type**: Class  
**Purpose**: Submit PR review

**Dependencies**:
- `GitHubRepository` - GitHub operations

**Method**:
- `suspend operator fun invoke(owner: String, repo: String, prNumber: Int, body: String?, event: ReviewEvent, comments: List<ReviewComment>): Result<Unit>` - Submit review

**Logic**:
1. Convert ReviewEvent enum to string
2. Delegate to repository

#### `ReviewEvent`
**Package**: `com.issuetrax.app.domain.usecase`  
**Type**: Enum  
**Purpose**: Review event types

**Values**:
- `APPROVE` - Approve PR
- `REQUEST_CHANGES` - Request changes
- `COMMENT` - Comment only

---

## 4. Data Layer

### 4.1 API Models (DTOs)

#### `UserDto`
**Package**: `com.issuetrax.app.data.api.model`  
**Type**: Data Class  
**Purpose**: GitHub API user response

**Annotations**: `@Serializable`

**Properties**:
- `id: Long` - User ID
- `login: String` - Username
- `name: String?` - Display name
- `email: String?` - Email
- `@SerialName("avatar_url") avatarUrl: String` - Avatar URL
- `@SerialName("html_url") htmlUrl: String` - Profile URL
- `type: String` - User type string

**Usage**: Deserialized from GitHub API, mapped to domain `User`

---

#### `RepositoryDto`
**Package**: `com.issuetrax.app.data.api.model`  
**Type**: Data Class  
**Purpose**: GitHub API repository response

**Annotations**: `@Serializable`

**Properties**:
- `id: Long` - Repository ID
- `name: String` - Repository name
- `@SerialName("full_name") fullName: String` - Full name
- `owner: UserDto` - Owner info
- `description: String?` - Description
- `private: Boolean` - Privacy flag
- `@SerialName("html_url") htmlUrl: String` - Web URL
- `@SerialName("clone_url") cloneUrl: String` - Clone URL
- `@SerialName("ssh_url") sshUrl: String` - SSH URL
- `@SerialName("default_branch") defaultBranch: String` - Default branch
- `language: String?` - Primary language
- `@SerialName("stargazers_count") stargazersCount: Int` - Stars
- `@SerialName("forks_count") forksCount: Int` - Forks
- `@SerialName("open_issues_count") openIssuesCount: Int` - Issues
- `@SerialName("created_at") createdAt: String` - Created timestamp
- `@SerialName("updated_at") updatedAt: String` - Updated timestamp
- `@SerialName("pushed_at") pushedAt: String?` - Pushed timestamp

**Usage**: Deserialized from GitHub API, mapped to domain `Repository`

---

#### `PullRequestDto`
**Package**: `com.issuetrax.app.data.api.model`  
**Type**: Data Class  
**Purpose**: GitHub API pull request response

**Annotations**: `@Serializable`

**Properties**:
- `id: Long` - PR ID
- `number: Int` - PR number
- `title: String` - Title
- `body: String?` - Description
- `state: String` - State string
- `user: UserDto` - Author
- `head: BranchDto` - Source branch
- `base: BranchDto` - Target branch
- `@SerialName("created_at") createdAt: String` - Created
- `@SerialName("updated_at") updatedAt: String` - Updated
- `@SerialName("merged_at") mergedAt: String?` - Merged
- `@SerialName("closed_at") closedAt: String?` - Closed
- `mergeable: Boolean?` - Mergeable status
- `merged: Boolean` - Merged flag
- `draft: Boolean` - Draft status
- `@SerialName("changed_files") changedFiles: Int` - Files changed
- `additions: Int` - Lines added
- `deletions: Int` - Lines deleted
- `commits: Int` - Commits
- `@SerialName("html_url") htmlUrl: String` - Web URL

**Nested Classes**:
- `BranchDto` - Branch information with ref

**Usage**: Deserialized from GitHub API, mapped to domain `PullRequest`

---

#### `FileDiffDto`
**Package**: `com.issuetrax.app.data.api.model`  
**Type**: Data Class  
**Purpose**: GitHub API file diff response

**Annotations**: `@Serializable`

**Properties**:
- `filename: String` - File path
- `status: String` - Status string
- `additions: Int` - Lines added
- `deletions: Int` - Lines deleted
- `changes: Int` - Total changes
- `patch: String?` - Diff patch
- `@SerialName("blob_url") blobUrl: String?` - Blob URL
- `@SerialName("raw_url") rawUrl: String?` - Raw URL
- `@SerialName("previous_filename") previousFilename: String?` - Previous name

**Usage**: Deserialized from GitHub API, mapped to domain `FileDiff`

---

### 4.2 API Service

#### `GitHubApiService`
**Package**: `com.issuetrax.app.data.api`  
**Type**: Interface  
**Purpose**: Retrofit service for GitHub REST API

**Endpoints**:
- `@GET("user")` `suspend fun getCurrentUser(@Header("Authorization") authorization: String): Response<UserDto>` - Get current user
- `@GET("user/repos")` `suspend fun getUserRepositories(@Header("Authorization") authorization: String, @Query("sort") sort: String = "updated", @Query("per_page") perPage: Int = 100): Response<List<RepositoryDto>>` - Get repositories
- `@GET("repos/{owner}/{repo}/pulls")` `suspend fun getPullRequests(@Header("Authorization") authorization: String, @Path("owner") owner: String, @Path("repo") repo: String, @Query("state") state: String = "open", @Query("per_page") perPage: Int = 100): Response<List<PullRequestDto>>` - Get PRs
- `@GET("repos/{owner}/{repo}/pulls/{number}")` `suspend fun getPullRequest(@Header("Authorization") authorization: String, @Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int): Response<PullRequestDto>` - Get single PR
- `@GET("repos/{owner}/{repo}/pulls/{number}/files")` `suspend fun getPullRequestFiles(@Header("Authorization") authorization: String, @Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int, @Query("per_page") perPage: Int = 100): Response<List<FileDiffDto>>` - Get PR files
- `@POST("repos/{owner}/{repo}/pulls/{number}/reviews")` `suspend fun createReview(@Header("Authorization") authorization: String, @Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int, @Body reviewRequest: CreateReviewRequest): Response<Unit>` - Submit review

#### `CreateReviewRequest`
**Package**: `com.issuetrax.app.data.api`  
**Type**: Data Class  
**Purpose**: Request body for creating a review

**Properties**:
- `body: String?` - Review comment
- `event: String` - Review event type
- `comments: List<ReviewCommentRequest>` - Line comments

#### `ReviewCommentRequest`
**Package**: `com.issuetrax.app.data.api`  
**Type**: Data Class  
**Purpose**: Request body for review comment

**Properties**:
- `path: String` - File path
- `position: Int` - Line position
- `body: String` - Comment text

---

### 4.3 Interceptors

#### `AuthInterceptor`
**Package**: `com.issuetrax.app.data.api`  
**Type**: Class  
**Purpose**: OkHttp interceptor for adding auth headers

**Annotations**: `@Inject`

**Implements**: `Interceptor`

**Dependencies**:
- `AuthRepository` - Access to tokens

**Method**:
- `override fun intercept(chain: Interceptor.Chain): Response` - Add auth header

**Logic**:
1. Check if auth header already exists
2. Get access token from repository
3. Add Bearer token to request
4. Proceed with request

**Note**: Uses `runBlocking` - consider for future optimization

---

#### `RateLimitInterceptor`
**Package**: `com.issuetrax.app.data.api`  
**Type**: Class  
**Purpose**: OkHttp interceptor for monitoring rate limits

**Annotations**: `@Inject`

**Implements**: `Interceptor`

**Method**:
- `override fun intercept(chain: Interceptor.Chain): Response` - Log rate limit info

**Logic**:
1. Proceed with request
2. Extract rate limit headers
3. Log rate limit status
4. Warn if rate limit exceeded

**Headers Monitored**:
- `X-RateLimit-Limit` - Total limit
- `X-RateLimit-Remaining` - Remaining calls
- `X-RateLimit-Reset` - Reset timestamp

---

### 4.4 Data Mappers

#### `DomainMappers`
**Package**: `com.issuetrax.app.data.mapper`  
**Type**: Kotlin File (Extension Functions)  
**Purpose**: Map DTOs to domain entities

**Functions**:
- `fun UserDto.toDomain(): User` - Map user
- `fun RepositoryDto.toDomain(): Repository` - Map repository
- `fun PullRequestDto.toDomain(): PullRequest` - Map PR
- `fun FileDiffDto.toDomain(): FileDiff` - Map file diff
- `private fun parseDateTime(dateString: String): LocalDateTime` - Parse ISO timestamps

**Logic**:
- Convert snake_case to camelCase
- Parse timestamps to LocalDateTime
- Map enum strings to domain enums
- Handle nullable fields

---

### 4.5 Repository Implementations

#### `GitHubRepositoryImpl`
**Package**: `com.issuetrax.app.data.repository`  
**Type**: Class  
**Purpose**: Implementation of GitHubRepository interface

**Annotations**: `@Inject`

**Implements**: `GitHubRepository`

**Dependencies**:
- `GitHubApiService` - API calls
- `AuthRepository` - Access token

**Methods**:
All methods follow this pattern:
1. Get access token
2. Make API call with Bearer token
3. Map DTO to domain entity
4. Return Result with success or failure

**Error Handling**:
- Token missing: Return failure
- API error: Return failure with HTTP code
- Exception: Return failure with exception

---

#### `AuthRepositoryImpl`
**Package**: `com.issuetrax.app.data.repository`  
**Type**: Class  
**Purpose**: Implementation of AuthRepository interface

**Annotations**: `@Inject`

**Implements**: `AuthRepository`

**Dependencies**:
- `@ApplicationContext Context` - For encrypted preferences

**Storage**: Uses `EncryptedSharedPreferences` with AES256_GCM encryption

**Key Management**: MasterKey with AES256_GCM scheme

**Properties**:
- `encryptedPrefs: SharedPreferences` - Encrypted storage
- `_isAuthenticatedFlow: MutableStateFlow<Boolean>` - Auth state

**Methods**:
- Token stored directly (Personal Access Token model)
- No OAuth flow implementation (simplified)
- No expiration handling

**Security**: 
- Tokens encrypted at rest
- Uses Android Keystore system

---

#### `RepositoryContextRepositoryImpl`
**Package**: `com.issuetrax.app.data.repository`  
**Type**: Class  
**Purpose**: Implementation of RepositoryContextRepository interface

**Annotations**: `@Inject`

**Implements**: `RepositoryContextRepository`

**Dependencies**:
- `@ApplicationContext Context` - For DataStore

**Storage**: Uses DataStore Preferences (not encrypted)

**Keys**:
- `OWNER_KEY` - Repository owner
- `REPO_KEY` - Repository name

**Methods**:
- Stores owner and repo as separate preferences
- Returns Flow of Pair or null
- Simple key-value storage

---

## 5. Resources (res folder)

### 5.1 Resource Structure

The `app/src/main/res` directory contains Android resources used throughout the application:

```
res/
├── drawable/          # Vector drawables and images
├── mipmap-*/         # App launcher icons (various densities)
├── values/           # Default values (strings, colors, themes)
├── values-v31/       # API 31+ specific values
└── xml/              # XML configuration files
```

### 5.2 String Resources

**File**: `res/values/strings.xml`  
**Purpose**: Centralized string resources for internationalization

**Categories**:

**App Identity**
- `app_name` - Application name: "Issuetrax"

**Authentication Strings**
- `auth_title` - "Welcome to Issuetrax"
- `auth_subtitle` - "Sign in with GitHub to start reviewing PRs"
- `auth_sign_in` - "Sign in with GitHub"
- `auth_error` - "Authentication failed. Please try again."

**Navigation Strings**
- `nav_current_work` - "Current Work"
- `nav_pr_review` - "PR Review"
- `nav_settings` - "Settings"

**Repository Selection Strings**
- `repo_selection_title` - "Select Repository"
- `repo_selection_subtitle` - "Choose a repository to work on"
- `repo_selection_empty` - "No repositories found"

**Current Work Strings**
- `current_work_title` - "Current Work"
- `current_work_no_active` - "No active work"
- `current_work_select_repo` - "Select Repository"

**PR Review Strings**
- `pr_review_title` - "Pull Request Review"
- `pr_review_files` - "Files"
- `pr_review_comments` - "Comments"
- `pr_review_submit` - "Submit Review"

**Common Strings**
- `loading` - "Loading…"
- `error_generic` - "Something went wrong. Please try again."
- `error_network` - "Network error. Check your connection."
- `retry` - "Retry"
- `cancel` - "Cancel"
- `ok` - "OK"

### 5.3 Color Resources

**File**: `res/values/colors.xml`  
**Purpose**: Material Design 3 color scheme and app-specific colors

**Material Design 3 Colors**
- Primary: `#1976D2` (GitHub-inspired blue)
- Secondary: `#575E71` (Neutral gray-blue)
- Tertiary: `#715574` (Purple accent)
- Error: `#BA1A1A` (Material error red)
- Background/Surface: `#FEFBFF` (Near white)
- Outline: `#74777F` (Border gray)

**GitHub-Specific Colors**
- `github_green` - `#238636` (Success/merged)
- `github_red` - `#D1242F` (Closed/conflicts)
- `github_blue` - `#0969DA` (Links/info)
- `github_purple` - `#8250DF` (Draft/special)

**Diff Colors**
- `diff_added` - `#E6FFED` (Addition background)
- `diff_added_border` - `#238636` (Addition border)
- `diff_removed` - `#FFEBE9` (Deletion background)
- `diff_removed_border` - `#D1242F` (Deletion border)
- `diff_modified` - `#FFF8C5` (Modified background)
- `diff_modified_border` - `#9A6700` (Modified border)

### 5.4 Theme Resources

**File**: `res/values/themes.xml`  
**Purpose**: Application theme configuration

**Theme**: `Theme.Issuetrax`
- Parent: `Theme.MaterialComponents.DayNight.NoActionBar`
- NoActionBar: Uses Compose-based app bars instead
- DayNight: Supports light/dark mode switching

**Theme Attributes**:
- Status bar color: Primary color
- Window background: Background color
- Material color mappings for primary, secondary, error, surface

**File**: `res/values-v31/themes.xml`  
**Purpose**: Android 12+ (API 31) specific theme configuration
- Splash screen customization
- Dynamic color support preparation

### 5.5 Drawable Resources

**File**: `res/drawable/ic_launcher_foreground.xml`  
**Purpose**: App launcher icon foreground layer

**Design**:
- Vector drawable (108x108dp)
- Green background (`#3DDC84` - Android green)
- Simplified list/document icon pattern
- Adaptive icon support for Android 8.0+

**Launcher Icons** (`res/mipmap-*/`):
- Multiple density variants: mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
- Adaptive icon support (`mipmap-anydpi-v26`)
- Background + foreground layers for Material Design

### 5.6 XML Configuration Files

#### Network Security Configuration
**File**: `res/xml/network_security_config.xml`  
**Purpose**: Network security policy for HTTPS connections

**Configuration**:
- Cleartext traffic: Disabled (HTTPS only)
- Certificate pinning for GitHub API
- Domains: `api.github.com`, `github.com` (with subdomains)
- Pin expiration: 2025-12-31
- Two SHA-256 certificate pins for redundancy

**Security Benefits**:
- Prevents man-in-the-middle attacks
- Ensures connection to legitimate GitHub servers
- Protects OAuth tokens in transit

#### Backup Rules
**File**: `res/xml/backup_rules.xml`  
**Purpose**: Android Auto Backup configuration

**Configuration**:
- Defines what data should be backed up
- Excludes sensitive data (tokens, preferences)

#### Data Extraction Rules
**File**: `res/xml/data_extraction_rules.xml`  
**Purpose**: Android 12+ data extraction rules

**Configuration**:
- Defines data extraction policies for device transfers
- Aligns with backup rules for consistency

### 5.7 Android Manifest

**File**: `app/src/main/AndroidManifest.xml`  
**Purpose**: Application configuration and permissions

**Permissions**:
- `INTERNET` - Required for GitHub API calls
- `ACCESS_NETWORK_STATE` - Check network connectivity

**Queries** (Android 11+):
- Intent query for HTTPS browsing (OAuth flow support)

**Application Configuration**:
- Application class: `IssuetraxApplication` (Hilt)
- Backup enabled with custom rules
- Network security config applied
- App icon and label from resources
- Material theme applied

**Activities**:
- `MainActivity` - Single activity, exported as launcher
- Launch mode: `singleTop` (prevents duplicate instances)

---

## 6. Dependency Injection

### 6.1 Hilt Modules

#### `NetworkModule`
**Package**: `com.issuetrax.app.di`  
**Type**: Object  
**Purpose**: Provides network dependencies

**Annotations**:
- `@Module` - Hilt module
- `@InstallIn(SingletonComponent::class)` - Application scope

**Provides**:
- `Json` - Kotlinx serialization config
- `HttpLoggingInterceptor` - Request/response logging
- `OkHttpClient` - HTTP client with interceptors
- `Retrofit` - Retrofit instance for GitHub API
- `GitHubApiService` - API service interface

**Configuration**:
- Base URL: `https://api.github.com/`
- Timeouts: 30 seconds (connect, read, write)
- Debug logging enabled in debug builds
- Auth and rate limit interceptors added

---

#### `RepositoryModule`
**Package**: `com.issuetrax.app.di`  
**Type**: Abstract Class  
**Purpose**: Binds repository implementations

**Annotations**:
- `@Module` - Hilt module
- `@InstallIn(SingletonComponent::class)` - Application scope

**Bindings**:
- `GitHubRepository` → `GitHubRepositoryImpl`
- `AuthRepository` → `AuthRepositoryImpl`
- `RepositoryContextRepository` → `RepositoryContextRepositoryImpl`

**Scope**: All singleton

---

#### `DatabaseModule`
**Package**: `com.issuetrax.app.di`  
**Type**: Object  
**Purpose**: Provides DataStore instance

**Annotations**:
- `@Module` - Hilt module
- `@InstallIn(SingletonComponent::class)` - Application scope

**Provides**:
- `DataStore<Preferences>` - Preferences DataStore

**Note**: Named "auth_preferences" but not used for auth (EncryptedPreferences used instead)

---

## 7. Class Relationships

### Dependency Flow

```
ViewModels
    ↓ (depends on)
Use Cases
    ↓ (depends on)
Repository Interfaces (Domain)
    ↑ (implements)
Repository Implementations (Data)
    ↓ (depends on)
API Service + DTOs + Mappers
```

### Key Patterns

1. **MVVM Pattern**: 
   - ViewModels expose StateFlow
   - UI observes state changes
   - UI sends events to ViewModel

2. **Repository Pattern**:
   - Domain defines interfaces
   - Data provides implementations
   - Abstracts data sources

3. **Use Case Pattern**:
   - Single responsibility
   - Encapsulates business logic
   - Called by ViewModels

4. **Dependency Injection**:
   - Hilt manages dependencies
   - Constructor injection
   - Singleton scoping

5. **Result Type**:
   - Success/Failure wrapping
   - Consistent error handling
   - No exceptions in domain

6. **Flow/StateFlow**:
   - Reactive data streams
   - Automatic UI updates
   - Lifecycle awareness

---

## 8. Current Implementation Notes

### What's Implemented

#### ✅ Foundation & Infrastructure
- Basic Clean Architecture structure (Presentation, Domain, Data layers)
- Hilt dependency injection setup
- Material Design 3 theme configuration
- Navigation graph with 4 routes
- Resource organization (strings, colors, themes)
- Network security configuration with certificate pinning
- Android manifest with proper permissions

#### ✅ Authentication Layer
- Personal Access Token authentication
- Encrypted token storage (EncryptedSharedPreferences)
- AuthRepository with token management
- AuthViewModel with reactive state
- AuthScreen UI with token input
- Auth state flow for automatic navigation

#### ✅ Data Layer
- GitHub REST API service (Retrofit)
- 6 API endpoints (user, repos, PRs, PR details, PR files, reviews)
- DTO models (UserDto, RepositoryDto, PullRequestDto, FileDiffDto)
- Domain mappers (DTO → Entity conversion)
- Auth interceptor for token injection
- Rate limit interceptor with logging
- Repository implementations (GitHub, Auth, RepositoryContext)

#### ✅ Domain Layer
- Entity models (User, Repository, PullRequest, FileDiff, CodeHunk, DiffLine)
- Enum types (UserType, PRState, ReviewDecision, FileStatus, LineType)
- Repository interfaces (GitHubRepository, AuthRepository, RepositoryContextRepository)
- Use cases (Authenticate, GetRepositories, GetPullRequests, SubmitReview)

#### ✅ Repository Selection Screen
- RepositorySelectionViewModel with state management
- Full UI implementation with repository list
- LazyColumn for repository items
- Repository details (name, description, language, open issues)
- Refresh functionality
- Loading, error, and empty states
- Navigation to Current Work screen

#### ⚠️ Current Work Screen - PARTIALLY IMPLEMENTED
**Implemented**:
- Screen skeleton with basic layout
- Owner/repo display
- Navigation structure to PR review
- Placeholder demo button

**Not Implemented (see detailed todo list below)**:
- Pull request list display
- PR filtering and sorting
- PR status indicators
- Refresh functionality
- Loading and error states
- Empty state handling

#### ⚠️ PR Review Screen - SKELETON ONLY
**Implemented**:
- Basic screen structure
- Top app bar with back navigation
- Owner/repo/PR number display
- Placeholder text

**Not Implemented**:
- PR metadata display
- File list
- Diff viewer
- Comment system
- Review submission
- Gesture navigation
- Any actual functionality

### What's Not Implemented

#### ❌ Core Features Not Started
- OAuth 2.0 flow (uses PAT instead)
- Local caching/offline support
- Gesture-based navigation system
- Code syntax highlighting
- Inline commenting on diffs
- Draft reviews
- Comment threading
- Review conversations
- File tree navigation
- Code search within PR
- Background sync
- Image/avatar loading
- Animations and transitions

#### ❌ Advanced Features
- Multiple repository support
- Issue management
- PR creation
- Branch management
- Merge conflict resolution
- CI/CD status display
- Review request notifications
- Team mentions
- Code suggestions

### Simplified Design Decisions

1. **No Room Database**: Direct API calls only, no local caching
2. **No WorkManager**: No background sync needed
3. **No Image Loading**: No Coil, avatars not displayed
4. **Personal Access Token**: No OAuth flow complexity
5. **Single Repository Focus**: Simplified workflow
6. **Minimal Dependencies**: Only essential libraries
7. **API 34 Only**: No backward compatibility code
8. **Compose Only**: No XML layouts
9. **No Complex Animations**: Focus on functionality

---

## 9. Testing Structure

### Current Test Coverage

**Note**: Detailed testing implementation is a future enhancement. The architecture supports testing with:

- ViewModels: Testable with coroutine test utilities
- Use Cases: Unit testable in isolation
- Repositories: Mockable interfaces
- Mappers: Pure functions, easily testable

---

## 10. Development Roadmap & Next Steps

This section provides a granular, step-by-step roadmap starting from the Current Work Screen. Each item is broken down into manageable tasks that can be implemented and tested independently.

### Phase 1: Complete Current Work Screen (Highest Priority)

#### 1.1 Update CurrentWorkViewModel
- [ ] Add method to load pull requests using existing `GetPullRequestsUseCase`
- [ ] Update `CurrentWorkUiState` to include:
  - [ ] `pullRequests: List<PullRequest>`
  - [ ] `isLoadingPRs: Boolean`
  - [ ] `filter: PRFilter` enum (ALL, OPEN, CLOSED)
  - [ ] `sortBy: PRSortOrder` enum (CREATED, UPDATED, COMMENTS)
- [ ] Implement `loadPullRequests(owner, repo, state)` method
- [ ] Add `refreshPullRequests()` method
- [ ] Add `filterPullRequests(filter)` method
- [ ] Handle loading states properly
- [ ] Handle error states with proper messages

#### 1.2 Update CurrentWorkScreen UI
- [ ] Replace placeholder content with pull request list
- [ ] Add `LazyColumn` for PR items (similar to repository list pattern)
- [ ] Create `PullRequestItem` composable with:
  - [ ] PR number and title
  - [ ] Author information (username)
  - [ ] State indicator (open/closed/merged) with colored badge
  - [ ] Basic stats (comments, changed files, +/- lines)
  - [ ] Created/updated timestamp
  - [ ] Click handler to navigate to PR review
- [ ] Add top app bar with:
  - [ ] Repository name display
  - [ ] Refresh button
  - [ ] Filter dropdown menu (open/closed/all)
- [ ] Implement loading state (CircularProgressIndicator)
- [ ] Implement error state with retry button
- [ ] Implement empty state ("No pull requests found")
- [ ] Add pull-to-refresh functionality

#### 1.3 Create Supporting Components
- [ ] `PRStateIndicator` composable - colored badge showing PR state
- [ ] `PRStats` composable - compact stats display
- [ ] `TimeAgo` helper function - format timestamps (e.g., "2 hours ago")

#### 1.4 Testing & Validation
- [ ] Test with repository that has no PRs
- [ ] Test with repository that has many PRs
- [ ] Test error handling (network errors, rate limits)
- [ ] Test filter functionality
- [ ] Test navigation to PR review screen with correct parameters

### Phase 2: Implement PR Review Screen - Basic Display

#### 2.1 Update PRReviewViewModel
- [ ] Wire up existing `loadPullRequest()` method to actual screen
- [ ] Ensure PR details are loaded on screen launch
- [ ] Ensure PR files are loaded on screen launch
- [ ] Handle loading states
- [ ] Handle error states
- [ ] Add computed property for current file metadata

#### 2.2 Create PR Metadata Section
- [ ] Create `PRMetadataCard` composable showing:
  - [ ] PR title
  - [ ] PR state (open/closed/merged)
  - [ ] Author information
  - [ ] Created/updated dates
  - [ ] Branch information (head → base)
  - [ ] PR description (body)
  - [ ] Stats (commits, files changed, additions, deletions)

#### 2.3 Implement File List View
- [ ] Create `FileListView` composable
- [ ] Display list of changed files using existing `files` from state
- [ ] For each file show:
  - [ ] File name with full path
  - [ ] Change type (added/modified/removed/renamed)
  - [ ] Addition/deletion counts
  - [ ] File status icon
- [ ] Highlight current file being viewed
- [ ] Add click handler to jump to specific file
- [ ] Show file counter (e.g., "File 1 of 12")

#### 2.4 Basic File Navigation
- [ ] Add navigation buttons (Previous/Next file)
- [ ] Implement `navigateToNextFile()` (already in ViewModel)
- [ ] Implement `navigateToPreviousFile()` (already in ViewModel)
- [ ] Show current file index
- [ ] Disable previous/next when at boundaries

#### 2.5 Testing & Validation
- [ ] Test with PR that has 1 file
- [ ] Test with PR that has many files
- [ ] Test file navigation
- [ ] Test error handling
- [ ] Verify data matches GitHub web interface

### Phase 3: Implement Diff Viewer (Core Feature)

#### 3.1 Create Diff Parser
- [ ] Create `DiffParser` utility class
- [ ] Parse unified diff format from `FileDiff.patch`
- [ ] Extract code hunks with line numbers
- [ ] Identify additions, deletions, context lines
- [ ] Handle special cases (binary files, no newline at EOF)

#### 3.2 Create Diff Display Components
- [ ] Create `DiffView` composable for file display
- [ ] Create `DiffHunk` composable for each hunk
- [ ] Create `DiffLine` composable for individual lines
- [ ] Apply color coding:
  - [ ] Green background for additions
  - [ ] Red background for deletions
  - [ ] Gray for context lines
- [ ] Show line numbers (old and new)
- [ ] Use monospace font for code
- [ ] Handle long lines with horizontal scroll

#### 3.3 Implement Inline Diff View
- [ ] Create `InlineDiffView` composable
- [ ] Show old and new lines together
- [ ] Optimize for mobile screen width
- [ ] Add expand/collapse for large hunks

#### 3.4 Testing & Validation
- [ ] Test with small diffs (1-5 lines)
- [ ] Test with large diffs (100+ lines)
- [ ] Test with various file types
- [ ] Test readability on mobile screen
- [ ] Compare with GitHub web interface

### Phase 4: Implement Review Submission

#### 4.1 Create Review UI Components
- [ ] Create `ReviewSubmissionDialog` composable
- [ ] Add review comment text field
- [ ] Add review type selector (Approve/Request Changes/Comment)
- [ ] Add submit button
- [ ] Handle submission loading state

#### 4.2 Wire Review Submission
- [ ] Connect to existing `submitReview()` in ViewModel
- [ ] Use existing `SubmitReviewUseCase`
- [ ] Show success feedback
- [ ] Handle submission errors
- [ ] Navigate back to Current Work on success

#### 4.3 Testing & Validation
- [ ] Test approve flow
- [ ] Test request changes flow
- [ ] Test comment-only flow
- [ ] Verify submission on GitHub web interface

### Phase 5: Add Gesture Navigation (Advanced)

#### 5.1 Implement Gesture Detection
- [ ] Create `GestureDetector` for swipe gestures
- [ ] Detect horizontal swipes (left/right)
- [ ] Detect vertical swipes (up/down)
- [ ] Add velocity thresholds
- [ ] Prevent conflicts with scroll

#### 5.2 Map Gestures to Actions
- [ ] Swipe left → Next file
- [ ] Swipe right → Previous file
- [ ] Swipe up → Next hunk (future)
- [ ] Swipe down → Previous hunk (future)

#### 5.3 Add Visual Feedback
- [ ] Show swipe indicators
- [ ] Animate file transitions
- [ ] Add haptic feedback

#### 5.4 Testing & Validation
- [ ] Test on physical device
- [ ] Test gesture sensitivity
- [ ] Test edge cases (end of files)

### Phase 6: Syntax Highlighting (Enhancement)

#### 6.1 Add Syntax Highlighting Library
- [ ] Evaluate lightweight syntax highlighting libraries
- [ ] Add dependency if needed
- [ ] Create language detector from file extension

#### 6.2 Implement Highlighting
- [ ] Apply syntax highlighting to diff lines
- [ ] Support common languages (Kotlin, Java, Python, JavaScript, etc.)
- [ ] Use appropriate color scheme for light/dark theme

#### 6.3 Testing & Validation
- [ ] Test with various file types
- [ ] Verify readability
- [ ] Check performance with large files

### Phase 7: Inline Comments (Future)

#### 7.1 Comment UI Components
- [ ] Create `CommentThread` composable
- [ ] Create `CommentItem` composable
- [ ] Create `AddCommentDialog` composable

#### 7.2 Comment Placement
- [ ] Allow long-press on diff line to add comment
- [ ] Show comment indicators on lines
- [ ] Display comment threads inline

#### 7.3 API Integration
- [ ] Add comment fetching to `GitHubApiService`
- [ ] Add comment posting to `GitHubApiService`
- [ ] Update domain models for comments
- [ ] Create use cases for comments

### Phase 8: Polish & Optimization

#### 8.1 Performance Optimization
- [ ] Optimize large PR loading
- [ ] Add pagination for file lists
- [ ] Implement lazy loading for diffs
- [ ] Add caching for viewed PRs

#### 8.2 Error Handling
- [ ] Improve error messages
- [ ] Add retry mechanisms
- [ ] Handle rate limiting gracefully
- [ ] Add offline detection

#### 8.3 Accessibility
- [ ] Add content descriptions
- [ ] Test with TalkBack
- [ ] Ensure proper touch targets
- [ ] Add keyboard navigation

#### 8.4 Testing
- [ ] Add unit tests for ViewModels
- [ ] Add unit tests for Use Cases
- [ ] Add UI tests for critical flows
- [ ] Add integration tests

### Priority Summary

**Immediate Next Steps** (This Sprint):
1. Complete Current Work Screen (Phase 1) - 3-5 days
2. Implement PR Review basic display (Phase 2) - 2-3 days

**Short Term** (Next Sprint):
3. Implement Diff Viewer (Phase 3) - 5-7 days
4. Implement Review Submission (Phase 4) - 2-3 days

**Medium Term** (Following Sprints):
5. Add Gesture Navigation (Phase 5) - 3-5 days
6. Add Syntax Highlighting (Phase 6) - 2-3 days

**Long Term** (Future):
7. Inline Comments (Phase 7) - 5-7 days
8. Polish & Optimization (Phase 8) - Ongoing

**Total Estimated Time to MVP**: 15-20 development days
**Total Estimated Time to Feature Complete**: 30-40 development days

---

## 11. Conclusion

This class architecture documentation reflects the **current implementation** of Issuetrax. The application follows Clean Architecture principles with a deliberately minimal approach:

- **Simple**: No over-engineering, only what's needed
- **Focused**: Single-repository PR review workflow
- **Modern**: Kotlin, Compose, Material Design 3
- **Testable**: Clear separation of concerns
- **Maintainable**: Standard patterns and practices

### Current State Summary

**Foundation**: ✅ Complete  
- Architecture, DI, navigation, resources, security all in place

**Authentication**: ✅ Complete  
- Full PAT authentication with encrypted storage

**Repository Selection**: ✅ Complete  
- Full UI, data loading, navigation working

**Current Work Screen**: ⚠️ 40% Complete  
- Structure in place, needs PR list implementation

**PR Review Screen**: ⚠️ 10% Complete  
- Basic structure only, core functionality needed

### Next Steps

Follow the granular roadmap in Section 10, starting with:
1. **Immediate**: Complete Current Work Screen (Phase 1)
2. **Short-term**: Implement PR Review basic display and diff viewer (Phases 2-3)
3. **Medium-term**: Add review submission and gesture navigation (Phases 4-5)

The architecture is designed to be understood step-by-step and can evolve incrementally as needs arise, without requiring significant refactoring.
