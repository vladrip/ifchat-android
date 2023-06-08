package com.vladrip.ifchat.data

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.haroldadmin.cnradapter.executeWithRetry
import com.vladrip.ifchat.api.IFChatService
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepository @Inject constructor(
    private val api: IFChatService,
) {

    suspend fun saveDeviceToken(deviceToken: String) {
        val body = deviceToken.toRequestBody("text/plain".toMediaType())
        executeWithRetry(times = Int.MAX_VALUE, initialDelay = 1000L) {
            api.saveDeviceToken(body)
        }
        Log.i(TAG, "saveDeviceToken($deviceToken)")
    }

    suspend fun deleteCurrentDeviceToken() {
        val deviceToken = Firebase.messaging.token.await()
        executeWithRetry(times = Int.MAX_VALUE, initialDelay = 1000L) {
            api.deleteDeviceToken(deviceToken)
        }
        Log.i(TAG, "deleteDeviceToken($deviceToken)")
    }

    companion object {
        private const val TAG = "MessagingRepository"
    }
}