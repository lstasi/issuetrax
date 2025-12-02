# Issue Templates

This directory contains GitHub issue templates for the Issuetrax project.

## Available Templates

### 1. Bug Report (`bug_report.yml`)
Use this template to report bugs or issues with the application.

**Fields:**
- What happened? (required)
- Steps to Reproduce (required)
- Version (required)
- Relevant log output
- Code of Conduct agreement

**Auto-assigned to:** `@copilot`  
**Label:** `bug`

### 2. Feature Request (`feature_request.yml`)
Use this template to suggest new features or enhancements.

**Fields:**
- Problem description (required)
- Proposed solution (required)
- Alternative solutions
- Additional context
- Code of Conduct agreement

**Auto-assigned to:** `@copilot`  
**Label:** `enhancement`

### 3. TODO / Task (`todo.yml`)
Use this template to create TODO items or tasks for tracking.

**Fields:**
- Task Description (required)
- Acceptance Criteria
- Priority (required)
- Additional Notes

**Auto-assigned to:** `@copilot`  
**Label:** `todo`

### 4. Blank Issue
You can also create a blank/custom issue without using a template. These issues will be automatically assigned to `@copilot` via the GitHub Actions workflow.

## Configuration

### `config.yml`
- **`blank_issues_enabled: true`**: Allows users to create blank issues in addition to template-based issues
- **`contact_links: []`**: No external links for issue creation

## Automatic Assignment

All issues (template-based and blank) are automatically assigned to `@copilot` through two mechanisms:

1. **Template-based issues**: Each template includes `assignees: - copilot` which automatically assigns the issue when created
2. **Blank/custom issues**: The `.github/workflows/assign-issue-to-copilot.yml` workflow automatically assigns new issues to `@copilot`

### Workflow Details

The `assign-issue-to-copilot.yml` workflow:
- Triggers on all new issues (`issues: types: [opened]`)
- Checks if `@copilot` is already assigned
- Adds `@copilot` as an assignee if not already assigned
- Provides detailed logging for debugging

## How to Use

### Creating an Issue

1. Go to the repository's Issues tab
2. Click "New Issue"
3. Choose one of the available templates or "Open a blank issue"
4. Fill out the required fields
5. Submit the issue

The issue will be automatically assigned to `@copilot` for tracking and management.

## Modifying Templates

To modify a template:

1. Edit the corresponding `.yml` file
2. Follow the [GitHub issue form schema](https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests/syntax-for-issue-forms)
3. Ensure `assignees: - copilot` is present if auto-assignment is desired
4. Commit and push changes

## Troubleshooting

If issues are not being assigned to `@copilot`:

1. Check the Actions tab for workflow runs
2. Review the `assign-issue-to-copilot.yml` workflow logs
3. Verify the `copilot` user exists and is a valid collaborator
4. Ensure the workflow has `issues: write` permissions

## References

- [GitHub Issue Forms Documentation](https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests/syntax-for-issue-forms)
- [GitHub Actions Issue Assignment](https://docs.github.com/en/rest/issues/assignees)
