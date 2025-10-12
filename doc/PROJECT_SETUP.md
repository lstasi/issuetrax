# Issuetrax - Project Setup Guide

## Documentation Overview

This guide covers project setup and initial implementation. For comprehensive documentation:
- **[CLASS_ARCHITECTURE.md](CLASS_ARCHITECTURE.md)** - Complete class reference with all classes, methods, and relationships
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Technical architecture patterns and design decisions
- **[PROJECT_DEFINITION.md](PROJECT_DEFINITION.md)** - Project requirements and roadmap
- **[UI_UX_DESIGN.md](UI_UX_DESIGN.md)** - Design specifications and gesture system

## Development Environment Setup

### Prerequisites

1. **Android Studio** (Latest stable version - Hedgehog 2023.1.1 or newer)
   - Download from: https://developer.android.com/studio
   - Include Android SDK, Platform Tools, and Build Tools

2. **Java Development Kit (JDK) 17**
   - OpenJDK 17 or Oracle JDK 17
   - Configure in Android Studio: File → Project Structure → SDK Location

3. **Kotlin Plugin** (Should be included with Android Studio)
   - Version 1.9.10 or newer

4. **Git**
   - For version control and GitHub integration

### Android SDK Requirements

```bash
# Minimum required SDK components:
- Android 14 (API 34) - Minimum SDK (No backward compatibility)
- Android 14 (API 34) - Target SDK
- Android SDK Build-Tools 34.0.0
- Android Emulator (for testing)
```

**Note**: This app targets Android 14+ exclusively for minimal complexity and dependencies.

## Project Structure Creation

### Quick SDK setup (non-IDE / CI)

If you're working on the project outside Android Studio (CI or local CLI), the build requires a `local.properties` file at the project root with the Android SDK path, for example:

```
sdk.dir=/home/you/Android/Sdk
```

This repository includes a small helper script to create `local.properties` automatically when an SDK is present on your machine or when `ANDROID_HOME` / `ANDROID_SDK_ROOT` is set:

```
./scripts/setup-local-properties.sh
```

Run the script once and it will write `local.properties` for you. `local.properties` is intentionally gitignored and should not be committed.


### 1. Create New Android Project

```bash
# Using Android Studio:
1. File → New → New Project
2. Select "Empty Activity" template
3. Configure project:
   - Name: Issuetrax
   - Package: com.issuetrax.app
   - Language: Kotlin
   - Minimum SDK: API 34 (Android 14)
   - Build configuration language: Kotlin DSL (build.gradle.kts)
   - Use Jetpack Compose: Yes
```

### 2. Project Directory Structure

```
Issuetrax/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/issuetrax/app/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── GitHubApiService.kt
│   │   │   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   │   │   └── ApiModels.kt
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── GitHubRepository.kt
│   │   │   │   │   │   ├── AuthRepository.kt
│   │   │   │   │   │   └── CacheRepository.kt
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── preferences/
│   │   │   │   │   │   └── cache/
│   │   │   │   │   └── models/
│   │   │   │   │       ├── PullRequest.kt
│   │   │   │   │       ├── Issue.kt
│   │   │   │   │       ├── Repository.kt
│   │   │   │   │       └── User.kt
│   │   │   │   ├── domain/
│   │   │   │   │   ├── entities/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── usecases/
│   │   │   │   │       ├── GetPullRequestsUseCase.kt
│   │   │   │   │       ├── SubmitReviewUseCase.kt
│   │   │   │   │       └── AuthenticateUseCase.kt
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── ui/
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   ├── AuthScreen.kt
│   │   │   │   │   │   │   └── AuthViewModel.kt
│   │   │   │   │   │   ├── current_work/
│   │   │   │   │   │   │   ├── CurrentWorkScreen.kt
│   │   │   │   │   │   │   └── CurrentWorkViewModel.kt
│   │   │   │   │   │   ├── pr_review/
│   │   │   │   │   │   │   ├── PRReviewScreen.kt
│   │   │   │   │   │   │   ├── PRReviewViewModel.kt
│   │   │   │   │   │   │   ├── components/
│   │   │   │   │   │   │   │   ├── InlineDiffViewer.kt
│   │   │   │   │   │   │   │   ├── GestureHandler.kt
│   │   │   │   │   │   │   │   ├── CommentDialog.kt
│   │   │   │   │   │   │   │   ├── FileNavigator.kt
│   │   │   │   │   │   │   │   └── SourceCodeBrowser.kt
│   │   │   │   │   │   │   └── gestures/
│   │   │   │   │   │   │       ├── PRReviewGestureDetector.kt
│   │   │   │   │   │   │       └── GestureExtensions.kt
│   │   │   │   │   │   ├── common/
│   │   │   │   │   │   │   ├── components/
│   │   │   │   │   │   │   ├── theme/
│   │   │   │   │   │   │   └── utils/
│   │   │   │   │   │   └── navigation/
│   │   │   │   │   │       ├── NavGraph.kt
│   │   │   │   │   │       ├── Routes.kt
│   │   │   │   │   │       └── NavigationExtensions.kt
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   ├── di/
│   │   │   │   │   ├── NetworkModule.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   ├── RepositoryModule.kt
│   │   │   │   │   └── UseCaseModule.kt
│   │   │   │   └── IssuetraxApplication.kt
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   ├── drawable/
│   │   │   │   ├── mipmap/
│   │   │   │   └── xml/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   │   └── java/com/issuetrax/app/
│   │   │       ├── data/
│   │   │       ├── domain/
│   │   │       └── presentation/
│   │   └── androidTest/
│   │       └── java/com/issuetrax/app/
│   │           ├── ui/
│   │           └── integration/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Gradle Configuration

### 1. Project-level build.gradle.kts

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
}
```

### 2. App-level build.gradle.kts

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

android {
    namespace = "com.issuetrax.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.issuetrax.app"
        minSdk = 34  // Android 14+ only - minimal app
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // GitHub OAuth Configuration
        buildConfigField("String", "GITHUB_CLIENT_ID", "\"your_github_client_id\"")
        buildConfigField("String", "GITHUB_REDIRECT_URI", "\"issuetrax://oauth\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug") // Use proper signing config for release
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Force consistent dependency versions to prevent update conflicts
configurations.all {
    resolutionStrategy {
        // Force consistent Kotlin version across all dependencies to avoid DEX conflicts
        force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
        force("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.21")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
        
        // Prefer newer versions to avoid conflicts
        preferProjectModules()
    }
}

dependencies {
    // MINIMAL DEPENDENCIES ONLY - Android 14+ 
    
    // Compose BOM - ensures compatible versions
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Activity & Lifecycle
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Networking - GitHub API only
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // DataStore - Simple preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Security & Authentication
    implementation("androidx.security:security-crypto:1.1.0")
    implementation("androidx.browser:browser:1.6.0")

    // Material Components - Theme support
    implementation("com.google.android.material:material:1.10.0")

    // Testing - Basic only
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.14.6")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Android Testing - Basic only
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debugging
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// NOTE: The following dependencies have been REMOVED for minimal app:
// ❌ androidx.compose.material:material-icons-extended (not used)
// ❌ io.coil-kt:coil-compose (no image loading needed)
// ❌ io.coil-kt:coil-svg (not used)
// ❌ androidx.room:* (no local database caching)
// ❌ androidx.work:* (no background sync)
// ❌ androidx.hilt:hilt-work (WorkManager not used)
// ❌ androidx.navigation:navigation-testing (minimal testing)
// ❌ androidx.work:work-testing (WorkManager not used)
// ❌ androidx.lifecycle:lifecycle-runtime-compose (not essential)

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
```

### 3. gradle.properties

```properties
# Project-wide Gradle settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true

# Kotlin code style for this project: "official" or "obsolete":
kotlin.code.style=official

# Enables namespacing of each library's R class so that its R class includes only the
# resources declared in the library itself and none from the library's dependencies,
# thereby reducing the size of the R class for that library
android.nonTransitiveRClass=true

# Enable Compose Compiler Metrics (optional, for performance analysis)
android.enableComposeCompilerMetrics=true
android.enableComposeCompilerReports=true
```

## GitHub OAuth Setup

### 1. GitHub OAuth App Registration

1. Go to GitHub Settings → Developer settings → OAuth Apps
2. Click "New OAuth App"
3. Fill in the details:
   ```
   Application name: Issuetrax
   Homepage URL: https://github.com/yourusername/Issuetrax
   Authorization callback URL: issuetrax://oauth
   ```
4. Note down the Client ID and Client Secret

### 2. Android Manifest Configuration

```xml
<!-- app/src/main/AndroidManifest.xml -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <queries>
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="https" />
        </intent>
    </queries>

    <application
        android:name=".IssuetraxApplication"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.Issuetrax"
        android:networkSecurityConfig="@xml/network_security_config">

        <!-- Disable unnecessary startup providers to avoid conflicts -->
        <provider
            android:name="androidx.startup.InitializationProvider"
            android:authorities="${applicationId}.androidx-startup"
            android:exported="false"
            tools:node="remove" />

        <activity
            android:name=".presentation.MainActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:theme="@style/Theme.Issuetrax">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            
            <!-- OAuth redirect handling -->
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="issuetrax" android:host="oauth" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### 3. Network Security Configuration

```xml
<!-- app/src/main/res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">api.github.com</domain>
        <pin-set expiration="2025-12-31">
            <pin digest="SHA-256">WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=</pin>
            <pin digest="SHA-256">RRM1dGqnDFsCJXBTHky16vi1obOlCgFFn/yOhI/y+ho=</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

## Initial Code Implementation

### 1. Application Class

```kotlin
// app/src/main/java/com/issuetrax/app/IssuetraxApplication.kt
package com.issuetrax.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IssuetraxApplication : Application()
```

### 2. Main Activity

```kotlin
// app/src/main/java/com/issuetrax/app/presentation/MainActivity.kt
package com.issuetrax.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.issuetrax.app.presentation.ui.common.theme.IssuetraxTheme
import com.issuetrax.app.presentation.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IssuetraxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
```

### 3. Navigation Setup

```kotlin
// app/src/main/java/com/issuetrax/app/presentation/navigation/Routes.kt
package com.issuetrax.app.presentation.navigation

sealed class Routes(val route: String) {
    object Auth : Routes("auth")
    object CurrentWork : Routes("current_work/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "current_work/$owner/$repo"
    }
    object PRReview : Routes("pr_review/{owner}/{repo}/{prNumber}") {
        fun createRoute(owner: String, repo: String, prNumber: Int) = 
            "pr_review/$owner/$repo/$prNumber"
    }
}
```

## Development Workflow

### 1. Version Control Setup

```bash
# Initialize git repository (if not done via GitHub)
git init
git add .
git commit -m "Initial project setup"

# Add GitHub remote
git remote add origin https://github.com/yourusername/Issuetrax.git
git push -u origin main
```

### 2. Development Branches

```bash
# Feature development workflow
git checkout -b feature/login-screen
# Develop feature...
git add .
git commit -m "Implement login screen with OAuth"
git push origin feature/login-screen
# Create PR on GitHub
```

### 3. Build & Test Commands

```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate debug APK
./gradlew assembleDebug
# APK location: app/build/outputs/apk/debug/

# Generate release APK (requires signing setup)
./gradlew assembleRelease
# APK location: app/build/outputs/apk/release/
```

## IDE Configuration

### 1. Android Studio Settings

1. **Code Style**: File → Settings → Editor → Code Style → Kotlin
   - Set from predefined style: "Kotlin Coding Conventions"

2. **Inspections**: File → Settings → Editor → Inspections
   - Enable Kotlin inspections
   - Enable Android inspections
   - Enable Compose inspections

3. **Live Templates**: File → Settings → Editor → Live Templates
   - Import Compose live templates

### 2. Useful Plugins

```
Recommended Android Studio Plugins:
- Kotlin (built-in)
- Compose Multiplatform IDE Support
- GitToolBox
- Hilt Inspector
- JSON To Kotlin Class
- Material Design Icon Generator
```

## Troubleshooting

### Common Issues

1. **Build Error: Unable to resolve dependency**
   ```bash
   # Solution: Clean and rebuild
   ./gradlew clean build
   ```

2. **Hilt Compilation Error**
   ```bash
   # Solution: Ensure kapt is properly configured
   # Check that @HiltAndroidApp is on Application class
   # Verify @AndroidEntryPoint is on MainActivity
   ```

3. **OAuth Redirect Not Working**
   ```bash
   # Solution: Verify AndroidManifest.xml intent-filter
   # Check GitHub OAuth app callback URL matches
   # Ensure scheme matches buildConfigField
   ```

### Performance Optimization

```bash
# Enable build optimizations in gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true

# Use build analyzer
./gradlew assembleDebug --scan
```

This setup guide provides everything needed to start implementing the Issuetrax Kotlin mobile app with the gesture-based PR review system.