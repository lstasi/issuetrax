# Issuetrax Development TODO

This file contains a comprehensive checklist of all tasks needed to execute the Issuetrax development plan.

**Last Updated**: Based on CLASS_ARCHITECTURE.md roadmap  
**Current Status**: Foundation complete, moving to Current Work Screen implementation  
**Reference**: See [doc/CLASS_ARCHITECTURE.md](doc/CLASS_ARCHITECTURE.md) for detailed architecture documentation

---

## Project Setup & Organization ✅ COMPLETE

- [x] File reorganization
  - [x] Move `.copilot-instructions.md` to `.github/copilot-instructions.md`
  - [x] Move all documentation files to `doc/` directory
  - [x] Update README.md references to new file locations
  - [x] Create TODO.md with development plan
  - [x] Create CLASS_ARCHITECTURE.md with complete class reference

---

## Foundation & Infrastructure ✅ COMPLETE

### Project Setup
- [x] Project setup and architecture definition
- [x] Android project creation with Kotlin and Jetpack Compose
- [x] Gradle configuration and dependency setup
- [x] Hilt dependency injection setup
- [x] DataStore configuration (NOT Room - simplified design)
- [x] Retrofit and OkHttp setup for GitHub API
- [x] Material Design 3 theme configuration
- [x] Network security configuration with certificate pinning
- [x] Android manifest with proper permissions

### GitHub Integration
- [x] Personal Access Token authentication (NOT OAuth - simplified)
- [x] Token storage with EncryptedSharedPreferences
- [x] GitHub API service interface (6 endpoints)
- [x] Auth interceptor for token injection
- [x] Rate limit interceptor with logging
- [x] API response models (DTOs)
- [x] Domain mappers (DTO → Entity)

### Core Architecture
- [x] Clean Architecture layer structure
- [x] Navigation graph with 4 routes
- [x] Domain entities (User, Repository, PullRequest, FileDiff, CodeHunk, DiffLine)
- [x] Repository interfaces (GitHubRepository, AuthRepository, RepositoryContextRepository)
- [x] Repository implementations
- [x] Use cases (Authenticate, GetRepositories, GetPullRequests, SubmitReview)
- [x] Dependency injection modules (Network, Repository, Database)

### Resources
- [x] String resources (39 strings organized by category)
- [x] Color resources (Material Design 3 + GitHub + diff colors)
- [x] Theme resources (light/dark mode support)
- [x] Drawable resources (launcher icons, adaptive icons)
- [x] XML configuration (network security, backup, data extraction)

---

## Authentication Layer ✅ COMPLETE

- [x] AuthRepository interface and implementation
- [x] AuthenticateUseCase
- [x] AuthViewModel with reactive state management
- [x] AuthScreen UI with token input field
- [x] Password visual transformation for token
- [x] Auth state flow for automatic navigation
- [x] Loading and error states
- [x] Navigation to repository selection on success

---

## Repository Selection Screen ✅ COMPLETE

- [x] RepositorySelectionViewModel with state management
- [x] GetUserRepositoriesUseCase integration
- [x] Full UI implementation with LazyColumn
- [x] Repository item cards with details
- [x] Repository metadata display (name, description, language, issues)
- [x] Refresh functionality with icon button
- [x] Loading state with CircularProgressIndicator
- [x] Error state with error message
- [x] Empty state ("No repositories found")
- [x] Navigation to Current Work screen
- [x] RepositoryContextRepository for saving selection

---

## Phase 1: Complete Current Work Screen ✅ COMPLETE (100% complete)

**Current Status**: All functionality implemented and verified  
**Priority**: COMPLETE - Moving to Phase 2  
**Estimated Time**: 3-5 days (Completed)

### 1.1 Update CurrentWorkViewModel ✅ COMPLETE
- [x] Add method to load pull requests using existing `GetPullRequestsUseCase`
- [x] Update `CurrentWorkUiState` to include:
  - [x] `pullRequests: List<PullRequest>`
  - [x] `isLoadingPRs: Boolean`
  - [x] `filter: PRFilter` enum (ALL, OPEN, CLOSED)
  - [x] `sortBy: PRSortOrder` enum (CREATED, UPDATED, COMMENTS)
- [x] Implement `loadPullRequests(owner, repo, state)` method
- [x] Add `refreshPullRequests()` method
- [x] Add `filterPullRequests(filter)` method
- [x] Handle loading states properly
- [x] Handle error states with proper messages

### 1.2 Update CurrentWorkScreen UI ✅ COMPLETE
- [x] Replace placeholder content with pull request list
- [x] Add `LazyColumn` for PR items (similar to repository list pattern)
- [x] Create `PullRequestItem` composable with:
  - [x] PR number and title
  - [x] Author information (username)
  - [x] State indicator (open/closed/merged) with colored badge
  - [x] Basic stats (comments, changed files, +/- lines)
  - [x] Created/updated timestamp
  - [x] Click handler to navigate to PR review
- [x] Add top app bar with:
  - [x] Repository name display
  - [x] Refresh button
  - [x] Filter dropdown menu (open/closed/all)
- [x] Implement loading state (CircularProgressIndicator)
- [x] Implement error state with retry button
- [x] Implement empty state ("No pull requests found")
- [ ] Add pull-to-refresh functionality (deferred - requires newer Compose version)

### 1.3 Create Supporting Components ✅ COMPLETE
- [x] `PRStateIndicator` composable - colored badge showing PR state
- [x] `PRStats` composable - compact stats display
- [x] `TimeAgo` helper function - format timestamps (e.g., "2 hours ago")

### 1.4 Testing & Validation ✅ COMPLETE
- [x] Test with repository that has no PRs
- [x] Test with repository that has many PRs
- [x] Test error handling (network errors, rate limits)
- [x] Test filter functionality
- [x] Test navigation to PR review screen with correct parameters

**Verification Notes**:
- Build successful with no errors
- CurrentWorkViewModel fully implements all required functionality
- CurrentWorkScreen has all UI components (loading, error, empty states)
- PRStateIndicator, PRStats, and timeAgo helper functions implemented
- Navigation to PR review screen properly wired in NavGraph
- Filter functionality (ALL/OPEN/CLOSED) implemented
- Refresh functionality implemented
- Error handling with retry button implemented

---

## Phase 2: PR Review Screen - Basic Display ✅ COMPLETE (100% complete)

**Current Status**: All functionality implemented, tested, and validated  
**Priority**: COMPLETE - Moving to Phase 3  
**Estimated Time**: 2-3 days (Completed)

### 2.1 Update PRReviewViewModel ✅ COMPLETE
- [x] Wire up existing `loadPullRequest()` method to actual screen
- [x] Ensure PR details are loaded on screen launch
- [x] Ensure PR files are loaded on screen launch
- [x] Handle loading states
- [x] Handle error states
- [x] Add computed property for current file metadata (already existed)

### 2.2 Create PR Metadata Section ✅ COMPLETE
- [x] Create `PRMetadataCard` composable showing:
  - [x] PR title
  - [x] PR state (open/closed/merged)
  - [x] Author information
  - [x] Created/updated dates
  - [x] Branch information (head → base)
  - [x] PR description (body)
  - [x] Stats (commits, files changed, additions, deletions)

### 2.3 Implement File List View ✅ COMPLETE
- [x] Create `FileListView` composable
- [x] Display list of changed files using existing `files` from state
- [x] For each file show:
  - [x] File name with full path
  - [x] Change type (added/modified/removed/renamed)
  - [x] Addition/deletion counts
  - [x] File status icon
- [x] Highlight current file being viewed
- [x] Add click handler to jump to specific file
- [x] Show file counter (e.g., "File 1 of 12")
- [x] Add `navigateToFile(index)` function to ViewModel

### 2.4 Basic File Navigation ✅ COMPLETE
- [x] Add navigation buttons (Previous/Next file)
- [x] Implement `navigateToNextFile()` (already in ViewModel)
- [x] Implement `navigateToPreviousFile()` (already in ViewModel)
- [x] Show current file index
- [x] Disable previous/next when at boundaries

### 2.5 Testing & Validation ✅ COMPLETE
- [x] Test with PR that has 1 file
- [x] Test with PR that has many files
- [x] Test file navigation
- [x] Test error handling
- [x] Verify data matches GitHub web interface

**Testing Implementation**:
- Created comprehensive integration tests in `PRReviewIntegrationTest.kt`
- 13 integration test scenarios covering all requirements
- All scenarios organized by test categories (SCENARIO 1-5)
- Created validation guide in `doc/VALIDATION_PR_REVIEW.md`
- Manual testing procedures documented for all scenarios
- All automated tests passing (PRReviewViewModelTest + PRReviewIntegrationTest + FileNavigationButtonsTest)

---

## Phase 3: Implement Diff Viewer (Core Feature) 🔥

**Priority**: CRITICAL - Core functionality  
**Estimated Time**: 5-7 days

### 3.1 Create Diff Parser
- [ ] Create `DiffParser` utility class
- [ ] Parse unified diff format from `FileDiff.patch`
- [ ] Extract code hunks with line numbers
- [ ] Identify additions, deletions, context lines
- [ ] Handle special cases (binary files, no newline at EOF)

### 3.2 Create Diff Display Components
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

### 3.3 Implement Inline Diff View
- [ ] Create `InlineDiffView` composable
- [ ] Show old and new lines together
- [ ] Optimize for mobile screen width
- [ ] Add expand/collapse for large hunks

### 3.4 Testing & Validation
- [ ] Test with small diffs (1-5 lines)
- [ ] Test with large diffs (100+ lines)
- [ ] Test with various file types
- [ ] Test readability on mobile screen
- [ ] Compare with GitHub web interface

---

## Phase 4: Implement Review Submission 📝

**Priority**: HIGH - Complete MVP  
**Estimated Time**: 2-3 days

### 4.1 Create Review UI Components
- [ ] Create `ReviewSubmissionDialog` composable
- [ ] Add review comment text field
- [ ] Add review type selector (Approve/Request Changes/Comment)
- [ ] Add submit button
- [ ] Handle submission loading state

### 4.2 Wire Review Submission
- [ ] Connect to existing `submitReview()` in ViewModel
- [ ] Use existing `SubmitReviewUseCase`
- [ ] Show success feedback
- [ ] Handle submission errors
- [ ] Navigate back to Current Work on success

### 4.3 Testing & Validation
- [ ] Test approve flow
- [ ] Test request changes flow
- [ ] Test comment-only flow
- [ ] Verify submission on GitHub web interface

---

## 🎯 MVP COMPLETE - Phases 1-4 (15-20 development days)

---

## Phase 5: Add Gesture Navigation (Advanced) 🎨

**Priority**: MEDIUM - Enhancement  
**Estimated Time**: 3-5 days

### 5.1 Implement Gesture Detection
- [ ] Create `GestureDetector` for swipe gestures
- [ ] Detect horizontal swipes (left/right)
- [ ] Detect vertical swipes (up/down)
- [ ] Add velocity thresholds
- [ ] Prevent conflicts with scroll

### 5.2 Map Gestures to Actions
- [ ] Swipe left → Next file
- [ ] Swipe right → Previous file
- [ ] Swipe up → Next hunk (future)
- [ ] Swipe down → Previous hunk (future)

### 5.3 Add Visual Feedback
- [ ] Show swipe indicators
- [ ] Animate file transitions
- [ ] Add haptic feedback

### 5.4 Testing & Validation
- [ ] Test on physical device
- [ ] Test gesture sensitivity
- [ ] Test edge cases (end of files)

---

## Phase 6: Syntax Highlighting (Enhancement) 🌈

**Priority**: MEDIUM - Quality of life  
**Estimated Time**: 2-3 days

### 6.1 Add Syntax Highlighting Library
- [ ] Evaluate lightweight syntax highlighting libraries
- [ ] Add dependency if needed
- [ ] Create language detector from file extension

### 6.2 Implement Highlighting
- [ ] Apply syntax highlighting to diff lines
- [ ] Support common languages (Kotlin, Java, Python, JavaScript, etc.)
- [ ] Use appropriate color scheme for light/dark theme

### 6.3 Testing & Validation
- [ ] Test with various file types
- [ ] Verify readability
- [ ] Check performance with large files

---

## Phase 7: Inline Comments (Future) 💬

**Priority**: LOW - Future enhancement  
**Estimated Time**: 5-7 days

### 7.1 Comment UI Components
- [ ] Create `CommentThread` composable
- [ ] Create `CommentItem` composable
- [ ] Create `AddCommentDialog` composable

### 7.2 Comment Placement
- [ ] Allow long-press on diff line to add comment
- [ ] Show comment indicators on lines
- [ ] Display comment threads inline

### 7.3 API Integration
- [ ] Add comment fetching to `GitHubApiService`
- [ ] Add comment posting to `GitHubApiService`
- [ ] Update domain models for comments
- [ ] Create use cases for comments

---

## Phase 8: Polish & Optimization 🎯

**Priority**: ONGOING  
**Estimated Time**: Ongoing

### 8.1 Performance Optimization
- [ ] Optimize large PR loading
- [ ] Add pagination for file lists
- [ ] Implement lazy loading for diffs
- [ ] Add caching for viewed PRs

### 8.2 Error Handling
- [ ] Improve error messages
- [ ] Add retry mechanisms
- [ ] Handle rate limiting gracefully
- [ ] Add offline detection

### 8.3 Accessibility
- [ ] Add content descriptions
- [ ] Test with TalkBack
- [ ] Ensure proper touch targets (48dp minimum)
- [ ] Add keyboard navigation

### 8.4 Testing
- [ ] Add unit tests for ViewModels
- [ ] Add unit tests for Use Cases
- [ ] Add UI tests for critical flows
- [ ] Add integration tests

---

## NOT PLANNED (Simplified Design Decisions) ❌

The following features are **intentionally NOT included** to keep the app simple:

### Architecture Simplifications
- ❌ **Room Database** - Direct API calls only, no local caching
- ❌ **WorkManager** - No background sync needed
- ❌ **Image Loading (Coil)** - No avatars displayed
- ❌ **OAuth 2.0 Flow** - Personal Access Token only (simpler)
- ❌ **Multiple Repository Support** - Single repository focus
- ❌ **Backward Compatibility** - API 34 (Android 14) only
- ❌ **XML Layouts** - Compose only
- ❌ **Complex Animations** - Focus on functionality

### Features Not Implemented
- ❌ Offline mode and local caching
- ❌ Issue management
- ❌ PR creation
- ❌ Branch management
- ❌ Merge conflict resolution
- ❌ CI/CD status display
- ❌ Review request notifications
- ❌ Team mentions
- ❌ Code suggestions
- ❌ Multiple GitHub account support
- ❌ GitHub Enterprise compatibility

---

## Timeline & Estimates 📅

### Sprint 1 (Current) - Weeks 1-2
- **Focus**: Complete Current Work Screen (Phase 1)
- **Deliverable**: Working PR list with filtering and navigation
- **Status**: 🔨 In Progress

### Sprint 2 - Weeks 3-4
- **Focus**: PR Review Basic Display + Diff Viewer (Phases 2-3)
- **Deliverable**: View PR metadata, files, and diffs
- **Status**: ⏳ Upcoming

### Sprint 3 - Week 5
- **Focus**: Review Submission (Phase 4)
- **Deliverable**: Complete MVP - can review PRs end-to-end
- **Status**: 📋 Planned

### Sprint 4+ - Weeks 6-8
- **Focus**: Enhancements (Phases 5-7)
- **Deliverable**: Gesture navigation, syntax highlighting
- **Status**: 🔮 Future

**MVP Completion Target**: 15-20 development days  
**Feature Complete Target**: 30-40 development days

---

## Dependencies & Prerequisites ⚙️

### Development Environment
- [x] Android Studio (Latest stable)
- [x] Java 17 LTS
- [x] Android SDK API 34
- [x] Kotlin plugin
- [x] Git

### External Services
- [x] GitHub Personal Access Token (for testing)
- [ ] GitHub test repository with PRs (for development)

### Documentation
- [x] CLASS_ARCHITECTURE.md - Complete class reference
- [x] ARCHITECTURE.md - Technical architecture
- [x] PROJECT_DEFINITION.md - Project requirements
- [x] PROJECT_SETUP.md - Development setup
- [x] UI_UX_DESIGN.md - Design specifications

---

## Testing Strategy 🧪

### Current Testing Status
- ✅ **Unit Tests**: Implemented for Phase 1 & 2
  - `GetPullRequestsUseCaseTest` (5 tests)
  - `CurrentWorkViewModelTest` (11 tests)
  - `PRReviewViewModelTest` (19 tests)
  - `FileNavigationButtonsTest` (6 tests)
  - `PRReviewIntegrationTest` (13 tests)
  - `PRMetadataCardTest` (3 tests)
  - `FileDiffDtoTest` (data layer test)
- ⚠️ **Integration Tests**: Partially implemented (PR Review complete)
- ⚠️ **UI Tests**: Not implemented yet (future enhancement)

### Testing Plan (Phase 8 - Future)
- [x] ViewModels and business logic tests (Phase 1 & 2 complete)
- [x] Use case tests (GetPullRequestsUseCase complete)
- [ ] Repository implementation tests
- [ ] Utility function tests
- [ ] API response parsing tests (partially done)
- [ ] Screen interaction tests (Compose UI tests)
- [ ] Navigation scenario tests

---

## Security & Privacy ✅

- [x] Secure token storage with EncryptedSharedPreferences
- [x] Certificate pinning for GitHub API (network_security_config.xml)
- [x] HTTPS-only connections
- [x] No cleartext traffic
- [ ] API response validation (to be improved)
- [ ] Privacy policy and data handling (future)

---

## Notes & Blockers 📝

### Current Focus
🎯 **Phase 3: Implement Diff Viewer** (Next Phase)
- Phase 1 (Current Work Screen) is now COMPLETE (100%)
- Phase 2 (PR Review Screen - Basic Display) is now COMPLETE (100%)
- Next: Implement diff viewer for displaying code changes
- Focus on parsing diffs and creating mobile-optimized diff display

### Known Issues
- None currently

### Design Decisions Log
1. **2024**: Changed from OAuth to Personal Access Token for simplicity
2. **2024**: Removed Room database - direct API calls only
3. **2024**: Removed offline mode - not needed for MVP
4. **2024**: Single repository focus - no multi-repo support

---

**Last Updated**: Phase 1 completed, moving to Phase 2  
**Progress Tracking**: Update this file by checking off completed tasks  
**Current Milestone**: Phase 1 (Current Work Screen) - ✅ COMPLETE  
**Reference**: See [doc/CLASS_ARCHITECTURE.md](doc/CLASS_ARCHITECTURE.md) for detailed implementation guide