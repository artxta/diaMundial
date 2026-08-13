@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.jose.diamundial"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.jose.diamundial"
        minSdk = 24
        targetSdk = 36
        versionCode = 12
        versionName = "1.0.0-12"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    bundle {
        language {
            enableSplit = false
        }
    }
    packaging {
        jniLibs {
            keepDebugSymbols.add("*/arm64-v8a/*.so")
            keepDebugSymbols.add("*/armeabi-v7a/*.so")
            keepDebugSymbols.add("*/x86/*.so")
            keepDebugSymbols.add("*/x86_64/*.so")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    lint {
        disable += "OldTargetApi"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.google.gson)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.inappupdate)
    implementation(libs.play.inappupdate.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}