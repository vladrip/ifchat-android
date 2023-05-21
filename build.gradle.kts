// Top-level build file where you can add configuration options common to all sub-projects/modules.
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
    }
}

plugins {
    id("com.android.application") version "8.0.0-beta05" apply false
    id("com.android.library") version "8.0.0-beta05" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.kapt") version "1.8.20"  apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
}