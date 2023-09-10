plugins {
    kotlin("android")
    id("com.android.application")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.vladrip.ifchat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vladrip.ifchat"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testApplicationId = "com.vladrip.test.ifchat"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    viewBinding {
        enable = true
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    val roomVersion = "2.6.0-beta01"
    val retrofitVersion = "2.9.0"
    val navigationVersion = "2.7.2"
    val hiltVersion = "2.48"
    val adaptingUnitsVersion = "1.1.0"

    //android, google
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")

    //navigation
    implementation("androidx.navigation:navigation-fragment:$navigationVersion")
    implementation("androidx.navigation:navigation-ui:$navigationVersion")

    //room, paging
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    implementation("androidx.paging:paging-runtime:3.2.1")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.github.haroldadmin:NetworkResponseAdapter:5.0.0")

    //hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    //adapting ui units
    implementation("com.intuit.sdp:sdp-android:$adaptingUnitsVersion")
    implementation("com.intuit.ssp:ssp-android:$adaptingUnitsVersion")

    //testing
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")
}