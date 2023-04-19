package com.vladrip.ifchat.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vladrip.ifchat.api.IFChatApi
import com.vladrip.ifchat.db.LocalDatabase
import javax.inject.Inject
import javax.inject.Singleton

const val MESSAGE_NETWORK_PAGE_SIZE = 20

@Singleton
class MessageRepository @Inject constructor(
    private val api: IFChatApi,
    private val localDb: LocalDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getMessagesByChatId(chatId: Long) = Pager(
        config = PagingConfig(MESSAGE_NETWORK_PAGE_SIZE),
        remoteMediator = MessageRemoteMediator(api, localDb, chatId)
        //TODO initialKey = last viewed key in chat
    ) {
        localDb.messageDao().getMessages(chatId)
    }.flow
}