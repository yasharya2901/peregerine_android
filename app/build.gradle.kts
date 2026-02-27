plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "me.yasharya.peregerine"
    compileSdk = 35

    defaultConfig {
        applicationId = "me.yasharya.peregerine"
        minSdk = 26
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
    buildFeatures {
        compose = true
    }
}
dependencies {
    // --- Room (SQLite) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // ✅ added: Flow + coroutines support for Room DAOs

    // ✅ added: Room codegen
    ksp(libs.androidx.room.compiler)
    

    // --- Core AndroidX ---
    implementation(libs.androidx.core.ktx)

    // Lifecycle runtime (you already had this)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ✅ added: ViewModel integration with Compose (viewModel(), etc.)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- Coroutines ---
    // ✅ added: Dispatcher/Main + Android coroutine support
    implementation(libs.kotlinx.coroutines.android)

    // --- Navigation (Compose) ---
    // ✅ added: NavHost / composable(...) etc.
    implementation(libs.androidx.navigation.compose)

    // --- Compose UI ---
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- Tests ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}