package com.vladrip.ifchat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IFChat : Application() {
    companion object {
        const val PREFS_FIREBASE = "firebase"
        const val PREFS_FIREBASE_DEVICE_TOKEN = "deviceToken"
    }
}