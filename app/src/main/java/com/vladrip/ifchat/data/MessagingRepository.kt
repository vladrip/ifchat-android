package com.vladrip.ifchat.data

import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.haroldadmin.cnradapter.executeWithRetry
import com.vladrip.ifchat.IFChat
import com.vladrip.ifchat.api.IFChatService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepository @Inject constructor(
    private val api: IFChatService,
    @ApplicationContext private val context: Context,
) {

    suspend fun saveDeviceToken(deviceToken: String) {
        val body = deviceToken.toRequestBody("text/plain".toMediaType())
        executeWithRetry(times = Int.MAX_VALUE, initialDelay = 500L) {
            api.saveDeviceToken(body)
        }
        Log.i(TAG, "saveDeviceToken($deviceToken)")
        context.getSharedPreferences(IFChat.PREFS_FIREBASE, 0).edit()
            .putString(IFChat.PREFS_FIREBASE_DEVICE_TOKEN, deviceToken)
            .apply()
    }

    suspend fun deleteCurrentDeviceToken() {
        val deviceToken = Firebase.messaging.token.await()
        executeWithRetry(times = Int.MAX_VALUE, initialDelay = 500L) {
            api.deleteDeviceToken(deviceToken)
        }
        Log.i(TAG, "deleteDeviceToken($deviceToken)")
        context.getSharedPreferences(IFChat.PREFS_FIREBASE, 0).edit()
            .putString(IFChat.PREFS_FIREBASE_DEVICE_TOKEN, null)
            .apply()
    }

    companion object {
        private const val TAG = "MessagingRepository"
    }
}