import java.util.Properties

plugins {
    id("com.android.application")
}

val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localPropsFile.inputStream().use { localProps.load(it) }
}
val tmdbApiKey = (localProps.getProperty("TMDB_API_KEY")
    ?: System.getenv("TMDB_API_KEY")
    ?: "").trim().replace("\\", "\\\\").replace("\"", "\\\"")

android {
    namespace = "com.yjh.cinetrack"
    compileSdk = 37
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.yjh.cinetrack"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = "0.2.0-step02"
        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
    }

    signingConfigs {
        create("localDebug") {
            storeFile = rootProject.file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("localDebug")
        }
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
}
