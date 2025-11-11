# Workflow Approval Process

## Overview

This repository uses GitHub Actions workflows for CI/CD. For security reasons, workflows that build and test code require manual approval before running on pull requests from first-time contributors or forks.

## Why Workflow Approval is Required

When a pull request comes from:
- A first-time contributor to this repository
- A forked repository

GitHub requires manual approval before running workflows. This is a security measure to prevent malicious code from being executed in the repository's CI/CD environment.

## For Contributors

### If Your PR Shows "Workflows Awaiting Approval"

1. **Don't worry!** This is normal behavior for first-time contributors.
2. A maintainer will review your changes.
3. Once the maintainer approves, the workflows will run automatically.
4. You can see the status in the **Actions** tab of the pull request.

### What Runs Automatically

The following workflows run automatically without approval:
- **PR Validation** (`pr-validation.yml`) - Basic PR validation and information
- **Assign Issue to Copilot** (`assign-issue-to-copilot.yml`) - Issue assignment

### What Requires Approval

The following workflows require approval for first-time contributors:
- **Android CI** (`android-ci.yml`) - Builds and tests the Android app
- **Docker Build** (`docker-build.yml`) - Builds Docker containers

## For Maintainers

### How to Approve Workflow Runs

1. **Go to the Actions tab** of the pull request
2. **Review the PR changes** carefully:
   - Check for any suspicious code
   - Verify the changes are legitimate
   - Look for unusual patterns or security concerns
3. **Click "Approve and run"** if the changes look safe
4. The workflows will then run automatically

### When to Approve

✅ **Safe to approve:**
- Documentation updates
- Bug fixes with clear intent
- Feature additions from known contributors
- Changes that don't modify build/test infrastructure

⚠️ **Review carefully:**
- Changes to workflow files (`.github/workflows/`)
- Changes to build scripts or configuration
- New dependencies or packages
- Code from completely new contributors

❌ **Do not approve:**
- Suspicious code patterns
- Obfuscated code
- Unexpected file changes
- PRs that don't match their description

### Adding Contributors to Allowlist

Once a contributor has made several approved contributions, you can add them to the repository collaborators or organization to bypass the approval requirement for future PRs.

## Troubleshooting

### "Workflows awaiting approval" but no runs to approve

This can happen when:
1. The workflow was triggered but GitHub hasn't created the run yet (wait a moment and refresh)
2. The workflow has conditions that prevent it from running (check workflow triggers)
3. The PR was updated, invalidating previous approval requests

**Solution:** The new PR Validation workflow will add a comment to the PR explaining the status and what needs to be done.

### Workflows not running after approval

If workflows don't run after approval:
1. Check if the PR branch is up to date with the base branch
2. Verify the workflow triggers match the PR conditions (branch, paths, etc.)
3. Check the workflow file for syntax errors
4. Look at the Actions tab for any error messages

## Additional Resources

- [GitHub Actions Security Best Practices](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)
- [Approving workflow runs from public forks](https://docs.github.com/en/actions/managing-workflow-runs/approving-workflow-runs-from-public-forks)
