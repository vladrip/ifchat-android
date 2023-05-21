package com.vladrip.ifchat.service

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.vladrip.ifchat.data.MessageRepository
import com.vladrip.ifchat.data.MessagingRepository
import com.vladrip.ifchat.model.Message
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    @Inject lateinit var messagingRepository: MessagingRepository
    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var gson: Gson
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        Log.i(TAG, "onNewToken($token)")
        scope.launch {
            while (true) {
                if (Firebase.auth.currentUser == null) break
                else delay(1000L)
            }
            messagingRepository.saveDeviceToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "From: ${message.from}")

        if (message.data.isNotEmpty()) {
            val messageBody = gson.fromJson(message.data["message"], Message::class.java)
            Log.d(TAG, "Message data payload: ${message.data["message"]}")
            if (messageBody == null) return
            scope.launch {
                messageRepository.saveMessageLocally(messageBody)
            }
        }

        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MessagingService"
    }
}