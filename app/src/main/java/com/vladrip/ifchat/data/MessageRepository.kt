package com.vladrip.ifchat.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.executeWithRetry
import com.vladrip.ifchat.api.IFChatService
import com.vladrip.ifchat.api.MESSAGE_NETWORK_PAGE_SIZE
import com.vladrip.ifchat.db.LocalDatabase
import com.vladrip.ifchat.model.Message
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val api: IFChatService,
    private val localDb: LocalDatabase,
) {
    private val messageDao = localDb.messageDao()

    @OptIn(ExperimentalPagingApi::class)
    fun getMessagesByChatId(chatId: Long) = Pager(
        config = PagingConfig(MESSAGE_NETWORK_PAGE_SIZE),
        remoteMediator = MessageRemoteMediator(api, localDb, chatId)
    ) {
        messageDao.getAll(chatId)
    }.flow

    suspend fun save(message: Message, createLocalTemp: Boolean) {
        var tempId: Long = 0
        if (createLocalTemp) {
            localDb.withTransaction {
                tempId = messageDao.getMaxId() + 19
                messageDao.insert(message.copy(id = tempId))
            }
        }

        val response = executeWithRetry(
            times = Int.MAX_VALUE,
            initialDelay = 500
        ) { api.saveMessage(message) }

        if (response is NetworkResponse.Success)
            response.headers["Location"]
                ?.substringAfterLast("/")
                ?.toLongOrNull()
                ?.also { realId ->
                    localDb.withTransaction {
                        if (createLocalTemp) messageDao.delete(tempId)
                        messageDao.insert(message.copy(id = realId, status = Message.Status.SENT))
                    }
                }
    }

    suspend fun delete(id: Long) {
        var isSending = false
        localDb.withTransaction {
            val message = messageDao.get(id)
            if (message.status == Message.Status.SENDING) {
                isSending = true
                messageDao.delete(id)
                return@withTransaction
            }
            messageDao.insert(message.copy(status = Message.Status.DELETING))
        }
        if (isSending) return

        val response = executeWithRetry(
            times = Int.MAX_VALUE,
            initialDelay = 500,
        ) { api.deleteMessage(id) }

        if (response is NetworkResponse.Success)
            messageDao.delete(id)
    }

    suspend fun saveMessageLocally(message: Message) {
        messageDao.insert(message)
    }
}