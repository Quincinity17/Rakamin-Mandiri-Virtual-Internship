plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.newsrakaminapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.newsrakaminapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"


        buildConfigField("String", "API_KEY", "\"a2ce544ab0d54f5fb474af12bf9387db\"")
        buildConfigField("String", "BASE_URL", "\"https://newsapi.org/v2/\"")

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

}

dependencies {
    // Core Android Libraries
    implementation(libs.androidx.core.ktx) // Core KTX extensions for Android
    implementation(libs.androidx.appcompat) // AppCompat for backward-compatible Android components
    implementation(libs.material) // Material Design components
    implementation(libs.androidx.activity) // Activity KTX extensions
    implementation(libs.androidx.constraintlayout) // Layout for designing responsive UI

    // Lifecycle and ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // ViewModel support with Kotlin extensions

    // Splash Screen API
    implementation(libs.androidx.core.splashscreen) // Splash Screen API for Android 12+

    // Image Loading and Transformation
    implementation(libs.glide) // Glide for image loading and caching
    implementation(libs.glide.transformations) // Additional image transformations for Glide

    // HTTP Client and Networking
    implementation(libs.retrofit) // Retrofit for API requests
    implementation(libs.converter.gson) // Gson converter for Retrofit
    implementation(libs.logging.interceptor) // Logging interceptor for HTTP requests

    // Shimmer Effect
    implementation(libs.shimmer) // Shimmer effect for loading placeholders

    // Material Design (specific version)
    implementation(libs.material.v190) // Specific version of Material Design components (v1.9.0)

    // Testing Dependencies
    testImplementation(libs.junit) // JUnit for unit testing
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit for instrumented tests
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
}
