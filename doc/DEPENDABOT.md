# Dependabot Configuration for Issuetrax

## Overview

This document describes the Dependabot configuration for the Issuetrax Android application, which is optimized for Android 14+ (API 34) compatibility and follows the project's minimal dependencies philosophy.

## Configuration File

The Dependabot configuration is located at `.github/dependabot.yml` and manages three ecosystems:
- **Gradle** (Android dependencies)
- **GitHub Actions** (CI/CD workflows)
- **Docker** (Container images)

## Gradle Dependencies (Android)

### Dependency Groups

Related dependencies are grouped together to ensure atomic updates and compatibility:

#### 1. Kotlin Ecosystem (`kotlin`)
- **Patterns**: `org.jetbrains.kotlin*`, `kotlinx-coroutines*`, `kotlinx-serialization*`
- **Update Types**: Minor and patch versions only
- **Rationale**: Keeps all Kotlin dependencies synchronized to avoid version conflicts

**Current Versions:**
- Kotlin: 2.0.21
- Coroutines: 1.7.3
- Serialization: 1.6.0

#### 2. AndroidX Compose (`compose`)
- **Patterns**: `androidx.compose*`
- **Update Types**: Minor and patch versions only
- **Rationale**: Compose BOM ensures compatible versions across all Compose libraries

**Current Version:**
- Compose BOM: 2023.10.01

#### 3. AndroidX Core Libraries (`androidx-core`)
- **Patterns**: Activity, Lifecycle, Navigation, DataStore, Security, Browser
- **Update Types**: Minor and patch versions only
- **Rationale**: Core Android libraries should be updated together

**Current Versions:**
- Activity: 1.8.0
- Lifecycle: 2.7.0
- Navigation: 2.7.4
- DataStore: 1.0.0
- Security: 1.1.0
- Browser: 1.6.0

#### 4. Hilt Dependency Injection (`hilt`)
- **Patterns**: `com.google.dagger:hilt*`, `androidx.hilt*`
- **Update Types**: Minor and patch versions only
- **Rationale**: Hilt core and navigation must stay in sync

**Current Version:**
- Hilt: 2.48

#### 5. Networking Stack (`networking`)
- **Patterns**: Retrofit, OkHttp, Serialization converters
- **Update Types**: Minor and patch versions only
- **Rationale**: Network stack components must be compatible

**Current Versions:**
- Retrofit: 2.9.0
- OkHttp: 4.12.0

#### 6. Testing Frameworks (`testing`)
- **Patterns**: JUnit, Coroutines Test, MockK, AndroidX Test
- **Update Types**: Minor and patch versions only
- **Rationale**: Testing dependencies should be updated together

**Current Versions:**
- JUnit: 4.13.2
- MockK: 1.14.6
- Coroutines Test: 1.7.3

### Version Constraints (Ignore Rules)

The following major version updates are **ignored** to maintain Android 14+ compatibility:

#### Kotlin (`org.jetbrains.kotlin:*`)
- **Ignored**: Major version updates
- **Rationale**: Stay on Kotlin 2.x for stable Android support
- **Impact**: Kotlin 3.x may introduce breaking changes

#### Android Gradle Plugin (`com.android.tools.build:gradle`)
- **Ignored**: Major version updates
- **Rationale**: AGP major updates require project-wide migration
- **Impact**: Ensures build system stability

#### Compose BOM (`androidx.compose:compose-bom`)
- **Ignored**: Major version updates
- **Rationale**: Compose major updates may break UI components
- **Impact**: Prevents unexpected UI regression

#### Hilt (`com.google.dagger:hilt*`)
- **Ignored**: Major version updates
- **Rationale**: Hilt major updates require annotation processing changes
- **Impact**: Prevents DI framework issues

#### Retrofit (`com.squareup.retrofit2:*`)
- **Ignored**: Major version updates
- **Rationale**: Stay on Retrofit 2.x for API stability
- **Impact**: Prevents network layer changes

#### OkHttp (`com.squareup.okhttp3:*`)
- **Ignored**: Major version updates
- **Rationale**: Stay on OkHttp 4.x for Android compatibility
- **Impact**: OkHttp 5.x may drop Android support

## GitHub Actions

### Configuration
- **Update Schedule**: Weekly on Mondays at 03:00
- **PR Limit**: 5 concurrent pull requests
- **Grouping**: All actions grouped together for batch updates

### Update Types
- Minor and patch updates are grouped and applied together
- Major version updates are evaluated individually

## Docker Dependencies

### Configuration
- **Update Schedule**: Weekly on Mondays at 03:00
- **PR Limit**: 5 concurrent pull requests

### Version Constraints

#### Eclipse Temurin JDK (`eclipse-temurin`)
- **Ignored**: Major version updates
- **Rationale**: Android Gradle Plugin 8.x requires Java 17
- **Current Version**: 17
- **Impact**: Ensures build environment compatibility

#### Nginx (`nginx`)
- **Ignored**: Major and minor version updates
- **Rationale**: Runtime stability for APK serving
- **Update Types**: Patch updates only
- **Impact**: Security patches only

## Schedule and Workflow

### Update Schedule
- **Day**: Monday
- **Time**: 03:00 UTC
- **Frequency**: Weekly

### Pull Request Management
- **Gradle PRs**: Maximum 10 concurrent
- **GitHub Actions PRs**: Maximum 5 concurrent
- **Docker PRs**: Maximum 5 concurrent

### Labels
All Dependabot PRs are automatically labeled for easy filtering:
- `dependencies` - All dependency updates
- `gradle` / `github-actions` / `docker` - Ecosystem-specific
- `android` - Android-related updates (Gradle)
- `ci-cd` - CI/CD related (Actions, Docker)

### Commit Message Format
All commits use conventional commit format:
```
chore(deps): <description>
```

### Reviewers
All PRs are automatically assigned to: `@lstasi`

## Android Compatibility Guidelines

### Minimum SDK Requirements
- **minSdk**: 34 (Android 14)
- **targetSdk**: 34 (Android 14)
- **compileSdk**: 34

### Philosophy
This project follows a **minimal dependencies** approach:
- No backward compatibility needed (minSdk = targetSdk = 34)
- Only essential libraries are included
- No Room database, WorkManager, or image loading libraries
- Focus on stability over bleeding edge

### Compatibility Verification
When reviewing Dependabot PRs:

1. **Check API Level Requirements**
   - Ensure new versions support API 34+
   - Verify no deprecated APIs are introduced

2. **Test Build Process**
   ```bash
   ./gradlew clean assembleDebug assembleRelease
   ```

3. **Run Tests**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

4. **Verify Docker Build**
   ```bash
   docker-compose build
   ```

5. **Check for Breaking Changes**
   - Review release notes for each dependency
   - Look for API changes or deprecations
   - Verify migration guides if needed

## Maintenance

### Reviewing Dependabot PRs

1. **Grouped Updates** (Recommended)
   - Review all dependencies in a group together
   - Test as a single unit
   - Merge if all tests pass

2. **Individual Updates**
   - Major updates arrive individually
   - Require careful review and testing
   - May need code changes

3. **Testing Checklist**
   - [ ] Build succeeds (debug and release)
   - [ ] All unit tests pass
   - [ ] All instrumentation tests pass
   - [ ] Docker build succeeds
   - [ ] No new deprecation warnings
   - [ ] App runs on target device/emulator

### Updating This Configuration

When modifying `.github/dependabot.yml`:

1. **Validate YAML Syntax**
   ```bash
   python3 -c "import yaml; yaml.safe_load(open('.github/dependabot.yml'))"
   ```

2. **Test Changes**
   - Commit to a feature branch
   - GitHub validates the configuration automatically
   - Wait for first scheduled run or trigger manually

3. **Document Changes**
   - Update this document with any new groups or ignore rules
   - Update version numbers if they change
   - Document the rationale for new constraints

## Troubleshooting

### Common Issues

#### Too Many Open PRs
- **Problem**: Dependabot exceeds PR limits
- **Solution**: Adjust `open-pull-requests-limit` in config
- **Impact**: Slows down update process

#### Conflicting Updates
- **Problem**: Dependencies require incompatible versions
- **Solution**: Add to ignore rules or update in sequence
- **Impact**: May require manual resolution

#### Build Failures
- **Problem**: New version breaks build
- **Solution**: Close PR, add to ignore rules temporarily
- **Impact**: Delays update until fix is available

### Getting Help

For issues with Dependabot configuration:
1. Check [Dependabot documentation](https://docs.github.com/en/code-security/dependabot)
2. Review [configuration options](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuration-options-for-the-dependabot.yml-file)
3. Consult Android dependency compatibility guides

## References

- [Dependabot Configuration Options](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuration-options-for-the-dependabot.yml-file)
- [Android Gradle Plugin Release Notes](https://developer.android.com/studio/releases/gradle-plugin)
- [Kotlin Release Notes](https://kotlinlang.org/docs/releases.html)
- [Jetpack Compose Release Notes](https://developer.android.com/jetpack/androidx/releases/compose)
- [Project Dependencies Documentation](MINIMAL_DEPENDENCIES.md)
