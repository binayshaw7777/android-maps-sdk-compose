import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
}

val olaMapsApiKey = localProperties.getProperty("OLA_MAPS_API_KEY", "")
val olaMapsSdkAarPath = localProperties.getProperty("OLA_MAPS_SDK_AAR")
    ?: System.getenv("OLA_MAPS_SDK_AAR")
    ?: ""

android {
    namespace = "com.ola.mapsdkdemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ola.mapsdkdemo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "OlaMapDemo_2.0.4-SDK-1.8.4"
    
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "OLA_MAPS_API_KEY", "\"$olaMapsApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    if (olaMapsSdkAarPath.isBlank()) {
        throw GradleException(
            "Missing Ola Maps SDK path. Set OLA_MAPS_SDK_AAR in local.properties or environment variables.",
        )
    }

    implementation(project(":ola-maps-compose"))
    implementation(files(olaMapsSdkAarPath))

    //Required for OlaMap SDK
    implementation (libs.maplibre.androidSdk)
    implementation (libs.maplibre.markerview)
    implementation (libs.maplibre.annotation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
