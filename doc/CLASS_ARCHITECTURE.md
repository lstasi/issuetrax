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

## 5. Dependency Injection

### 5.1 Hilt Modules

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

## 6. Class Relationships

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

## 7. Current Implementation Notes

### What's Implemented
✅ Basic Clean Architecture structure  
✅ GitHub API integration (REST API)  
✅ Authentication with Personal Access Token  
✅ Repository selection  
✅ Pull request listing  
✅ Pull request file viewing  
✅ Basic review submission  
✅ Material Design 3 theme  
✅ Navigation between screens  
✅ Encrypted token storage  

### What's Not Implemented
❌ OAuth 2.0 flow (uses PAT instead)  
❌ Local caching/offline support  
❌ Gesture-based navigation  
❌ Code syntax highlighting  
❌ Inline commenting  
❌ Draft reviews  
❌ Background sync  
❌ Image loading  
❌ Complex animations  

### Simplified Design Decisions

1. **No Room Database**: Direct API calls only, no local caching
2. **No WorkManager**: No background sync needed
3. **No Image Loading**: No Coil, avatars not displayed
4. **Personal Access Token**: No OAuth flow complexity
5. **Single Repository Focus**: Simplified workflow
6. **Minimal Dependencies**: Only essential libraries
7. **API 34 Only**: No backward compatibility code

---

## 8. Testing Structure

### Current Test Coverage

**Note**: Detailed testing implementation is a future enhancement. The architecture supports testing with:

- ViewModels: Testable with coroutine test utilities
- Use Cases: Unit testable in isolation
- Repositories: Mockable interfaces
- Mappers: Pure functions, easily testable

---

## 9. Future Enhancements

Based on the architecture, these are potential future additions (not currently planned):

1. Gesture system implementation
2. Syntax highlighting for code diffs
3. Inline comment threading
4. Draft review support
5. Local caching with Room
6. Offline mode
7. More comprehensive error handling
8. Advanced navigation patterns

---

## Conclusion

This class architecture documentation reflects the **current implementation** of Issuetrax. The application follows Clean Architecture principles with a deliberately minimal approach:

- **Simple**: No over-engineering, only what's needed
- **Focused**: Single-repository PR review workflow
- **Modern**: Kotlin, Compose, Material Design 3
- **Testable**: Clear separation of concerns
- **Maintainable**: Standard patterns and practices

The architecture is designed to be understood step-by-step and can evolve incrementally as needs arise, without requiring significant refactoring.
