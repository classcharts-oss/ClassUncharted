import java.io.ByteArrayOutputStream

plugins {
    id("com.android.application")
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "2.0.10-1.0.24"
}

val getCommitHash = { ->
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }

    stdout.toString().trim()
}

android {
    namespace = "stupidrepo.classuncharted"
    compileSdk = 35

    defaultConfig {
        applicationId = "stupidrepo.classuncharted"
        minSdk = 28
        targetSdk = 35

        getCommitHash().let { commitHash ->
            versionName = commitHash
            versionCode = 1
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.biometric)

    annotationProcessor("androidx.room:room-compiler:${libs.versions.roomCommonVersion.get()}")
    ksp("androidx.room:room-compiler:${libs.versions.roomCommonVersion.get()}")

    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)

    androidTestImplementation(composeBom)

    implementation(libs.material3)
    implementation(libs.material)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.foundation)
    implementation(libs.ui)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)

    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)

    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.okhttp)
    implementation(libs.gson)

    implementation(libs.androidx.core.ktx)
}