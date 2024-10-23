// build.gradle (Project level)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Pastikan Anda menggunakan versi yang terbaru untuk plugin Google services
    id("com.google.gms.google-services") version "4.3.15" apply false
}


