# Workflow Approval Implementation

## Overview

This document describes the implementation of GitHub Actions workflow approval functionality in Issuetrax.

## Problem Statement

GitHub Actions workflows from first-time contributors or external forks often require manual approval before they can run. This feature allows repository maintainers to approve these workflow runs directly from the Issuetrax mobile app.

## Implementation

### Architecture

The implementation follows Clean Architecture principles with clear separation of concerns:

```
Presentation Layer (UI)
    ↓
Domain Layer (Use Cases)
    ↓
Data Layer (Repository & API)
```

### Components

#### 1. Domain Layer

**WorkflowRun Entity**
```kotlin
data class WorkflowRun(
    val id: Long,
    val name: String,
    val status: String,
    val conclusion: String?,
    val headSha: String,
    val htmlUrl: String
)
```

**Use Cases**
- `GetWorkflowRunsUseCase`: Fetches workflow runs for a repository
- `ApproveWorkflowRunUseCase`: Approves a specific workflow run

#### 2. Data Layer

**API Endpoints**
- `GET /repos/{owner}/{repo}/actions/runs` - List workflow runs
- `POST /repos/{owner}/{repo}/actions/runs/{run_id}/approve` - Approve workflow run

**DTOs**
- `WorkflowRunDto`: API response model
- `WorkflowRunsResponseDto`: Wrapper for list of workflow runs
- `WorkflowRunApprovalResponseDto`: Approval response

#### 3. Presentation Layer

**PRReviewViewModel**
- `loadWorkflowRuns(owner, repo)`: Loads workflow runs filtered by "waiting" status
- `approveWorkflowRun(owner, repo)`: Approves the first waiting workflow run

**PRReviewUiState**
- Added `workflowRuns: List<WorkflowRun>` to track workflow run state

**PRReviewScreen**
- Loads workflow runs on screen launch
- Wires up approve workflow button to ViewModel

**PRActionToolbar**
- Already had the approve workflow button placeholder
- Now functional and calls `onApproveWorkflow` callback

## User Flow

1. User navigates to a PR review screen
2. App automatically loads workflow runs that require approval
3. User sees the "Play" icon button in the toolbar (for open PRs only)
4. User taps the button to approve the first waiting workflow run
5. App displays success message via snackbar
6. Workflow runs are automatically reloaded to reflect the new state

## API Usage

### Get Workflow Runs
```
GET /repos/{owner}/{repo}/actions/runs?event=pull_request&status=waiting
```

Response includes:
- List of workflow runs
- Each run has: id, name, status, conclusion, head_sha, html_url

### Approve Workflow Run
```
POST /repos/{owner}/{repo}/actions/runs/{run_id}/approve
```

Returns:
- Status and required approval count

## Error Handling

- **No waiting runs**: Shows message "No workflow runs require approval"
- **API failure**: Shows error message with details
- **Network error**: Displays appropriate error in UI
- **Silent failures**: Loading workflow runs fails silently (optional data)

## Testing

### Unit Tests

**Use Case Tests** (7 tests)
- `ApproveWorkflowRunUseCaseTest` (3 tests)
- `GetWorkflowRunsUseCaseTest` (4 tests)

**ViewModel Tests** (8 tests)
- `WorkflowApprovalTest` (8 scenarios)
  - Loading workflow runs
  - Approving workflow runs
  - Handling errors
  - Edge cases (empty lists, no waiting runs)

All tests pass successfully (100% pass rate).

## Security Considerations

- Uses personal access token for authentication
- All API calls are authenticated with Bearer token
- No sensitive data stored locally
- Follows GitHub API rate limiting guidelines

## Future Enhancements

1. **Bulk Approval**: Allow approving multiple workflow runs at once
2. **Workflow Details**: Show detailed information about each workflow run
3. **Status Indicators**: Display visual indicators for workflow run status
4. **Notifications**: Notify users when workflows require approval
5. **Filtering**: Allow filtering workflow runs by name or status

## References

- [GitHub Actions API Documentation](https://docs.github.com/en/rest/actions/workflow-runs)
- [Workflow Run Approval](https://docs.github.com/en/rest/actions/workflow-runs#approve-a-workflow-run-for-a-fork-pull-request)
- [Clean Architecture Principles](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

## Files Changed

### New Files
- `app/src/main/java/com/issuetrax/app/domain/entity/WorkflowRun.kt`
- `app/src/main/java/com/issuetrax/app/domain/usecase/GetWorkflowRunsUseCase.kt`
- `app/src/main/java/com/issuetrax/app/domain/usecase/ApproveWorkflowRunUseCase.kt`
- `app/src/main/java/com/issuetrax/app/data/api/model/WorkflowRunDto.kt`
- `app/src/test/java/com/issuetrax/app/domain/usecase/GetWorkflowRunsUseCaseTest.kt`
- `app/src/test/java/com/issuetrax/app/domain/usecase/ApproveWorkflowRunUseCaseTest.kt`
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/WorkflowApprovalTest.kt`

### Modified Files
- `app/src/main/java/com/issuetrax/app/data/api/GitHubApiService.kt`
- `app/src/main/java/com/issuetrax/app/data/mapper/DomainMappers.kt`
- `app/src/main/java/com/issuetrax/app/data/repository/GitHubRepositoryImpl.kt`
- `app/src/main/java/com/issuetrax/app/domain/repository/GitHubRepository.kt`
- `app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/PRReviewViewModel.kt`
- `app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/PRReviewScreen.kt`
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/PRReviewViewModelTest.kt`
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/PRReviewIntegrationTest.kt`

## Summary

The workflow approval feature is fully implemented, tested, and ready for use. It follows the project's architectural patterns and coding standards, with comprehensive test coverage and proper error handling.
