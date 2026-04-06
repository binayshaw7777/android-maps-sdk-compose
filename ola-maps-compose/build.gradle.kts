import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
}

val olaMapsSdkAarPath = localProperties.getProperty("OLA_MAPS_SDK_AAR")
    ?: System.getenv("OLA_MAPS_SDK_AAR")
    ?: ""

android {
    namespace = "com.ola.maps.compose"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            consumerProguardFiles("consumer-rules.pro")
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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    if (olaMapsSdkAarPath.isBlank()) {
        throw GradleException(
            "Missing Ola Maps SDK path. Set OLA_MAPS_SDK_AAR in local.properties or environment variables.",
        )
    }

    compileOnly(files(olaMapsSdkAarPath))

    implementation(libs.maplibre.androidSdk)
    implementation(libs.maplibre.markerview)
    implementation(libs.maplibre.annotation)

    implementation(libs.androidx.compose.bom)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    testImplementation(files(olaMapsSdkAarPath))
}
