package com.vladrip.ifchat.api

import com.vladrip.ifchat.data.MessageRepository
import com.vladrip.ifchat.db.LocalDatabase
import com.vladrip.ifchat.model.Message
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRestorer @Inject constructor(
    private val messageRepository: MessageRepository,
    private val localDb: LocalDatabase,
) {
    private val inProgressMessageStatuses = listOf(
        Message.Status.SENDING, Message.Status.DELETING
    )

    suspend fun restoreRequests() {
        val messageDao = localDb.messageDao()
        val messages = messageDao
            .getFilteredByStatuses(inProgressMessageStatuses.map { it.name })
        messages.forEach {
            when (it.status) {
                Message.Status.SENDING -> messageRepository.save(it, false)
                Message.Status.DELETING -> messageRepository.delete(it.id)
                else -> {}
            }
        }
    }
}