// Top-level build file where you can add configuration options common to all sub-projects/modules.
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
    }
}

plugins {
    id("com.android.application") version "8.2.0-beta01" apply false
    id("com.android.library") version "8.2.0-beta01" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.10"  apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
}