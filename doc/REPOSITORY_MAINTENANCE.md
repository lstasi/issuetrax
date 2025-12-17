# Repository Maintenance Guide

This document provides important information for repository maintainers and contributors working on the Issuetrax project.

## Personal Access Token (PAT) Scope Requirements

When working with this repository, especially when merging Pull Requests or making changes to certain protected files, you need to ensure your Personal Access Token has the appropriate scopes.

### Required Scopes for Different Operations

#### Standard Development (Reading code, creating PRs, commenting)
- `repo` - Full control of private repositories
- `user` - Read user profile data

#### Workflow File Modifications (Creating/modifying files in `.github/workflows/`)
When merging PRs or pushing commits that create or modify workflow files in `.github/workflows/`, you need:
- `repo` - Full control of private repositories
- `user` - Read user profile data
- **`workflow`** - Update GitHub Action workflows ⚠️ **REQUIRED**

### Why is the `workflow` Scope Required?

GitHub has repository protection rules that prevent Personal Access Tokens from creating or updating workflow files (`.github/workflows/*.yml`) without the `workflow` scope. This is a security feature to prevent unauthorized modification of CI/CD pipelines.

**Error you might see without this scope:**
```
Failed to merge PR: 405 - Repository rule violations found
refusing to allow a Personal Access Token to create or update workflow `.github/workflows/android-ci.yml` without `workflow` scope
```

## Creating a PAT with Proper Scopes

### For Repository Maintainers

If you need to merge PRs that modify workflow files, follow these steps:

1. Go to GitHub Settings: https://github.com/settings/tokens
2. Click "Generate new token" → "Generate new token (classic)"
3. Give it a descriptive name (e.g., "Issuetrax Maintenance Token")
4. Set an appropriate expiration (recommendation: 90 days, then rotate)
5. Select the following scopes:
   - ✅ `repo` (Full control of private repositories)
     - This includes: `repo:status`, `repo_deployment`, `public_repo`, `repo:invite`, `security_events`
   - ✅ `workflow` (Update GitHub Action workflows)
   - ✅ `user` (Read user profile data)
     - This includes: `read:user`, `user:email`, `user:follow`
6. Click "Generate token"
7. **Important**: Copy the token immediately (it won't be shown again)
8. Store it securely (e.g., in a password manager)

### For Regular Contributors

If you're only creating PRs and not merging them, you can use a token with just:
- `repo` - Full control of private repositories
- `user` - Read user profile data

The repository maintainers will handle merging PRs that modify workflows.

## Common Scenarios

### Scenario 1: Adding a New GitHub Actions Workflow
**Who**: Repository maintainer
**Required scopes**: `repo`, `workflow`, `user`
**Action**: Create/merge PR with new workflow file

### Scenario 2: Modifying Existing Workflow Files
**Who**: Repository maintainer
**Required scopes**: `repo`, `workflow`, `user`
**Action**: Edit and merge changes to `.github/workflows/*.yml`

### Scenario 3: Regular Code Changes
**Who**: Any contributor
**Required scopes**: `repo`, `user`
**Action**: Create PR with code changes (not affecting workflows)

### Scenario 4: Merging Regular PRs
**Who**: Repository maintainer
**Required scopes**: `repo`, `user`
**Action**: Merge PRs that don't modify workflow files

## Troubleshooting

### "Repository rule violations found" Error

**Problem**: You tried to merge a PR that modifies workflow files, but your PAT doesn't have the `workflow` scope.

**Solution**:
1. Create a new PAT with the `workflow` scope (see instructions above)
2. Update your git credentials with the new token
3. Retry the merge operation

### Updating Git Credentials

If you need to update your PAT:

**Using Git Credential Manager (recommended)**:
```bash
# Clear cached credentials (enter the following lines interactively)
git credential reject
# Then type these lines followed by a blank line:
protocol=https
host=github.com

# Next git operation will prompt for credentials
git pull
# Enter your username and the new PAT as the password
```

**Alternative: Using heredoc**:
```bash
git credential reject <<EOF
protocol=https
host=github.com

EOF

# Next git operation will prompt for credentials
git pull
```

**⚠️ NOT RECOMMENDED: Embedding token in URL**:
```bash
# WARNING: This exposes the token in git config and shell history
# Only use for testing/temporary setups
git remote set-url origin https://NEW_TOKEN@github.com/lstasi/issuetrax.git

# If you use this method, clear your shell history afterwards:
history -d $(history 1)
```

## Security Best Practices

1. **Rotate tokens regularly**: Set expiration dates and rotate tokens every 90 days
2. **Use separate tokens**: Consider using different tokens for different purposes
3. **Minimum privilege**: Only grant the scopes you actually need
4. **Store securely**: Use a password manager to store tokens
5. **Revoke unused tokens**: Regularly audit and revoke tokens you're no longer using
6. **Never commit tokens**: Never commit PATs to the repository
7. **Review token usage**: Periodically review what tokens have access to your account

## For Automated Systems

If you're setting up automation (CI/CD, bots, etc.) that needs to modify workflows:

1. Create a dedicated service account or use GitHub Apps instead of PATs when possible
2. Grant only the minimum required scopes
3. Use repository secrets to store tokens securely
4. Use environment-specific tokens (dev, staging, prod)
5. Implement token rotation policies
6. Monitor token usage through GitHub's audit logs

## Additional Resources

- [GitHub Personal Access Tokens Documentation](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
- [GitHub Token Scopes Reference](https://docs.github.com/en/developers/apps/building-oauth-apps/scopes-for-oauth-apps)
- [Repository Rules](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-rulesets/about-rulesets)
- [Managing Your Personal Access Tokens](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens)

## Questions or Issues?

If you encounter issues related to PAT scopes or repository access:

1. Check this documentation first
2. Verify your PAT has the required scopes at https://github.com/settings/tokens
3. Try regenerating your PAT with the correct scopes
4. If problems persist, open an issue in the repository
