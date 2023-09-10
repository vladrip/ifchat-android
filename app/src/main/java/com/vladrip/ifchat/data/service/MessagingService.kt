package com.vladrip.ifchat.data.service

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.vladrip.ifchat.data.entity.Message
import com.vladrip.ifchat.data.repository.MessageRepository
import com.vladrip.ifchat.data.repository.MessagingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var messagingRepository: MessagingRepository
    @Inject
    lateinit var messageRepository: MessageRepository
    @Inject
    lateinit var gson: Gson
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        Log.i(TAG, "onNewToken($token)")
        scope.launch {
            while (Firebase.auth.currentUser == null) delay(1000L)
            messagingRepository.saveDeviceToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "From: ${message.from}")

        if (message.data.isNotEmpty()) {
            val messageBody =
                gson.fromJson(message.data["message"], Message::class.java) ?: return
            scope.launch {
                messageRepository.saveMessageLocally(messageBody)
            }
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