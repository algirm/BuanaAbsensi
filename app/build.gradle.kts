plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.papb.buanaabsensi"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.swipeRefreshLayout)
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
//    implementation("androidx.appcompat:appcompat:1.2.0")
//    implementation("com.google.android.material:material:1.3.0")

    testImplementation(Testing.junit4)

    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)

    // Timber
    implementation(JakeWharton.timber)

    // Easy Permissions
    implementation("pub.devrel:easypermissions:3.0.0")

    // Google Maps Location Services
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation("com.google.android.gms:play-services-maps:17.0.1")

    // Dagger - Hilt
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.android.compiler)
    kapt(AndroidX.hilt.compiler)

    // Firebase
    implementation("com.google.firebase:firebase-analytics:18.0.3")
    implementation("com.google.firebase:firebase-auth:20.0.4")
    implementation("com.google.firebase:firebase-firestore:22.1.2")
    implementation("com.google.firebase:firebase-storage:19.2.2")

    // Room
    implementation(AndroidX.room.runtime)
    kapt(AndroidX.room.compiler)
    implementation(AndroidX.room.ktx)

    // Picasso
    implementation(Square.picasso)

    // Circle Image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Coroutines
    implementation(KotlinX.coroutines.core)
    implementation(KotlinX.coroutines.android)
    implementation(KotlinX.coroutines.playServices)

    // Lifecycle
    implementation(AndroidX.lifecycle.commonJava8)
    implementation(AndroidX.lifecycle.runtimeKtx)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.lifecycle.service)

    // Navigation
    implementation(AndroidX.navigation.uiKtx)
    implementation(AndroidX.navigation.fragmentKtx)
}