
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.faizan.widgetstudio"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.faizan.widgetstudio"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.3")

    testImplementation("junit:junit:4.13.2")
}
