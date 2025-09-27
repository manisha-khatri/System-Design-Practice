plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.parcelize)
}

android {
    namespace = "com.example.systemdesignpracticeapps"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.systemdesignpracticeapps"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    hilt {
        enableAggregatingTask = false
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.work.runtime)

    // Core AndroidX Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Networking & Serialization
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Coroutines & ViewModel
    implementation(libs.coroutine.viewmodel) // androidx.lifecycle:lifecycle-viewmodel-ktx
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.lifecycle.runtime.compose) // lifecycle-runtime-compose

    // Jetpack Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.core.testing)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.runtime.livedata)

    // Navigation Compose
    implementation(libs.compose.navigation) // androidx.navigation:navigation-compose
    implementation(libs.androidx.navigation.fragment) // This is for fragments, might not be needed if solely Compose Navigation.

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // KSP for Dagger Hilt
    implementation(libs.androidx.hilt.navigation.compose) // For hiltViewModel() in Compose
    ksp(libs.androidx.hilt.compiler) // ✅ KSP for androidx.hilt compiler

    // DataStore
    implementation(libs.datastore.preferences)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler) // KSP for Room

    // Glide
    implementation(libs.glide)
    ksp(libs.glide.compiler) // if using Kotlin annotation processing

    implementation(libs.coil.compose)


    // Apollo GraphQL
    implementation(libs.apollo.runtime)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockk)
    testImplementation(libs.core.testing)
    testImplementation(libs.coroutines.test)

    // Instrumented Testing
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.rules)

    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.contrib)
    androidTestImplementation(libs.espresso.intents)

    androidTestImplementation(libs.ui.automator)

    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(libs.hamcrest)

    // Flow testing library
    testImplementation(libs.turbine)
    androidTestImplementation(libs.turbine)

    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

}