package com.vladrip.ifchat.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vladrip.ifchat.api.IFChatApi
import com.vladrip.ifchat.db.LocalDatabase
import com.vladrip.ifchat.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

const val MESSAGE_NETWORK_PAGE_SIZE = 30

@Singleton
class MessageRepository @Inject constructor(
    private val api: IFChatApi,
    private val localDb: LocalDatabase
) {
    private val messageDao = localDb.messageDao()

    @OptIn(ExperimentalPagingApi::class)
    fun getMessagesByChatId(chatId: Long) = Pager(
        config = PagingConfig(MESSAGE_NETWORK_PAGE_SIZE),
        remoteMediator = MessageRemoteMediator(api, localDb, chatId)
        //TODO initialKey = last viewed key in chat
    ) {
        localDb.messageDao().getMessages(chatId)
    }.flow

    suspend fun saveMessage(message: Message) {
        api.saveMessage(message)

        val nextId = withContext(Dispatchers.Default) { messageDao.getMaxId() + 1 }
        Log.i("SAVING_MESSAGE", "$nextId, ${message.content}")
        messageDao.insert(message.copy(id = nextId))
    }
}