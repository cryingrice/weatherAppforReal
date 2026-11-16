import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Load local.properties at configuration time
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(FileInputStream(file))
    }
}

val mapboxAccessToken: String = localProperties.getProperty("MAPBOX_ACCESS_TOKEN", "")

android {
    namespace = "com.example.weatherappforreal"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.weatherappforreal"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Injects mapbox_access_token as R.string.mapbox_access_token at build time
        resValue("string", "mapbox_access_token", mapboxAccessToken)
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}
// ... dependencies