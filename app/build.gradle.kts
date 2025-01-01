// build.gradle.kts (App level)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.valetparking"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.valetparking"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-storage-ktx:20.2.0")

    testImplementation(libs.junit)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.github.1902shubh:SendMail:1.0.0")
    implementation ("com.hbb20:ccp:2.5.0")

    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

}
