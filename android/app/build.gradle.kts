plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mr.buses"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mr.buses"
        minSdk = 29
        targetSdk = 34
        versionCode = 12
        versionName = "4.9"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../mr-buses.keystore")
            storePassword = "mrbuses2026"
            keyAlias = "mrbuses"
            keyPassword = "mrbuses2026"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
