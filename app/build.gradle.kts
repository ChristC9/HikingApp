plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")   // Kotlin 2.x + Compose compiler
    id("org.jetbrains.kotlin.kapt")            // for Room (or switch to KSP later)
}

android {
    namespace = "com.example.hikingappuogfinal"
    compileSdk = 35                           // <-- REQUIRED

    defaultConfig {
        applicationId = "com.example.hikingappuogfinal"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures { compose = true }          // enable Compose

    // Do NOT set composeOptions.kotlinCompilerExtensionVersion with Kotlin 2.x + compose plugin

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Material XML themes (for Theme.MHike)
    implementation("com.google.android.material:material:1.12.0")

    // Room (SQLite) + kapt
    val room = "2.6.1"
    implementation("androidx.room:room-runtime:$room")
    implementation("androidx.room:room-ktx:$room")
    kapt("androidx.room:room-compiler:$room")

    // Kotlinx datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
}
