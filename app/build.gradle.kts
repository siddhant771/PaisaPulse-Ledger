import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")

    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.paisapulse.app"
    compileSdk = 37 // Optimized to a stable production SDK target

    defaultConfig {
        applicationId = "com.paisapulse.app"
        minSdk = 26
        targetSdk = 35 // Aligned with modern platform standards
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

        // Secure API Key Loading Layer
        val localProperties = Properties().apply {
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) {
                localFile.inputStream().use { stream ->
                    this@apply.load(stream)
                }
            }
        }

        val apiKey = project.findProperty("GEMINI_API_KEY") as? String
            ?: System.getenv("GEMINI_API_KEY")
            ?: localProperties.getProperty("GEMINI_API_KEY", "")

        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
        val apiBaseUrl = project.findProperty("API_BASE_URL") as? String
            ?: localProperties.getProperty("API_BASE_URL", "http://10.0.2.2:8000/")
        val deviceApiToken = project.findProperty("DEVICE_API_TOKEN") as? String
            ?: localProperties.getProperty("DEVICE_API_TOKEN", "")
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "DEVICE_API_TOKEN", "\"$deviceApiToken\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                listOf("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    testImplementation(libs.junit)
    kaptTest("junit:junit:4.13.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Jetpack Compose UI Engine
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // Dagger Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.ui.graphics)
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0") // FIXED: Added missing AndroidX Hilt Compiler!

    // Room Local SQLite Cache
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")

    // Coroutines & Reactive Streams
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Google GenAI SDK for Gemini Core
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    // Retrofit & Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0") // Moshi is preferred for Kotlin
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // OkHttp & Logging
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.18")
}
