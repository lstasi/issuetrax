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
You can also create a blank/custom issue without using a template.

**Note:** Blank issues will not be automatically assigned to `@copilot`. To ensure proper assignment, please use one of the templates above.

## Configuration

### `config.yml`
- **`blank_issues_enabled: true`**: Allows users to create blank issues in addition to template-based issues
- **`contact_links: []`**: No external links for issue creation

## Automatic Assignment

Template-based issues are automatically assigned to `@copilot` through GitHub's native issue template API:

- Each template includes `assignees: - copilot` in its YAML configuration
- When you create an issue using a template, GitHub's API automatically assigns the specified users
- This happens instantly when the issue is created, without requiring any additional workflows or actions

## How to Use

### Creating an Issue

1. Go to the repository's Issues tab
2. Click "New Issue"
3. Choose one of the available templates or "Open a blank issue"
4. Fill out the required fields
5. Submit the issue

If you use a template, the issue will be automatically assigned to `@copilot` for tracking and management.

## Modifying Templates

To modify a template:

1. Edit the corresponding `.yml` file
2. Follow the [GitHub issue form schema](https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests/syntax-for-issue-forms)
3. Ensure `assignees: - copilot` is present if auto-assignment is desired
4. Commit and push changes

## Troubleshooting

If template-based issues are not being assigned to `@copilot`:

1. Verify the template YAML file includes `assignees: - copilot` 
2. Ensure the `copilot` user exists and is a valid collaborator in the repository
3. Check that you selected a template (not a blank issue) when creating the issue
4. Verify the template YAML syntax is correct

## References

- [GitHub Issue Forms Documentation](https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests/syntax-for-issue-forms)
- [GitHub Issue Template Assignees](https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests/syntax-for-issue-forms#top-level-syntax)
