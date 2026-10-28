plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ink.suivichantier"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.ink.suivichantier"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}
