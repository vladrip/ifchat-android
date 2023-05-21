pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}

rootProject.name = "ifchat"
include(":app")
