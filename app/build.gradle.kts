import com.android.build.gradle.api.ApkVariantOutput
import java.io.File

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
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
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Load keystore from environment variables (same as GitHub Actions)
    // Required environment variables:
    //   SIGNING_KEY_BASE64 - Base64-encoded keystore file
    //   KEY_ALIAS - Key alias
    //   KEY_STORE_PASSWORD - Keystore password
    //   KEY_PASSWORD - Key password
    signingConfigs {
        create("release") {
            // Signing configuration now uses a file on disk for easier local use and CI.
            // Use either the SIGNING_KEY_FILE environment variable (absolute or relative path),
            // or a default file located at the repository root: `signing-key.jks`.
            val signingKeyFileEnv = System.getenv("SIGNING_KEY_FILE")
            val repoRootKeystore = File(rootProject.projectDir, "signing-key.jks")
            val keystoreFile = when {
                signingKeyFileEnv != null && signingKeyFileEnv.isNotBlank() -> File(rootProject.projectDir, signingKeyFileEnv)
                repoRootKeystore.exists() -> repoRootKeystore
                else -> null
            }

            val envKeyAlias = System.getenv("KEY_ALIAS")
            val envStorePassword = System.getenv("KEY_STORE_PASSWORD")
            val envKeyPassword = System.getenv("KEY_PASSWORD")

            if (keystoreFile != null && keystoreFile.exists() && envKeyAlias != null && envStorePassword != null && envKeyPassword != null) {
                storeFile = keystoreFile
                storePassword = envStorePassword
                keyAlias = envKeyAlias
                keyPassword = envKeyPassword
                println("✓ Using keystore file: ${keystoreFile.absolutePath}")
            }
            else {
                println("WARNING: Release signing config is not fully configured. Release APKs will be unsigned (debug signing may be used).")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Only set signing config if a storeFile is present (env-based signing created it)
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null) {
                signingConfig = releaseSigning
            } else {
                println("NOTICE: Release signing config has no storeFile; release APK will be unsigned (debug signing may be used).")
            }
        }
    }

    applicationVariants.all {
        outputs.all {
            val apkOutput = this as? ApkVariantOutput
            val versionName = defaultConfig.versionName
            
            // Set output file name for release builds
            // The file will be unsigned initially and signed by CI/CD pipeline
            if (buildType.name == "release" && apkOutput != null) {
                apkOutput.outputFileName = "issuetrax-v${versionName}-unsigned.apk"
            }
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

    lint {
        // Disable problematic lint checks that have bugs in Android Lint tool
        disable += "MutableCollectionMutableState"
        disable += "AutoboxingStateCreation"
        // Disable MissingClass for manifest provider removal (tools:node="remove")
        disable += "MissingClass"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

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
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // DataStore (for preferences)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Security (for encrypted preferences)
    implementation("androidx.security:security-crypto:1.1.0")

    // Custom Tabs (for OAuth)
    implementation("androidx.browser:browser:1.6.0")

    // Material Components (needed for Theme.MaterialComponents parent and material attributes)
    implementation("com.google.android.material:material:1.13.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("io.mockk:mockk:1.14.6")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}