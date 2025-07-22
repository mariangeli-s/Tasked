plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.tasked"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tasked"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Material Design (para componentes de UI modernos)
    implementation(libs.material) // Verifica la última versión estable
    // Retrofit (para llamadas a la API REST)
    implementation(libs.retrofit)
    // Convertidor de Gson para Retrofit (para parsear JSON)
    implementation(libs.converter.gson)
    // OkHttp (para interceptores, logging de red, etc.)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    // ViewModel y LiveData (componentes de arquitectura MVVM)
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // Para ViewModel en Kotlin
    implementation(libs.androidx.lifecycle.livedata.ktx) // Para LiveData en Kotlin
    implementation(libs.androidx.activity.ktx) // Para by viewModels() en Activity
    // Coroutines (para manejar operaciones asíncronas de forma más sencilla)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Jetpack Compose
    implementation(libs.androidx.compose.bom.v20240600) // Verifica la última versión estable
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.activity.compose.v190) // Para Activity con Compose
    implementation(libs.androidx.lifecycle.runtime.ktx.v283) // Para collectAsStateWithLifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose) // Para viewModels() en Composable


    // SharedPreferences (para guardar el token y rol localmente) - ya viene con Android, no necesita dependencia extra, pero la menciono para el concepto.
}