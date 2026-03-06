plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.paisapulse.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.paisapulse.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.filament.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation("androidx.room:room-runtime:2.7.0-alpha02")
    implementation("androidx.room:room-ktx:2.7.0-alpha02")
    kapt("androidx.room:room-compiler:2.7.0-alpha02")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    implementation("androidx.sqlite:sqlite:2.4.0")
    testImplementation("junit:junit:4.13.2")
    implementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.0.21")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}