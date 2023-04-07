package com.vladrip.ifchat.model.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vladrip.ifchat.model.api.IFChatApi
import com.vladrip.ifchat.model.db.LocalDatabase
import com.vladrip.ifchat.model.entity.Chat
import com.vladrip.ifchat.model.entity.Chat.ChatType
import com.vladrip.ifchat.model.entity.Person
import javax.inject.Inject
import javax.inject.Singleton

const val CHAT_LIST_NETWORK_PAGE_SIZE = 20

@Singleton
class ChatRepository @Inject constructor(
    api: IFChatApi,
    localDb: LocalDatabase
) : BaseRepository(api, localDb) {
    private val chatDao = localDb.chatDao()
    private val chatListDao = localDb.chatListDao()
    private val personDao = localDb.personDao()

    @OptIn(ExperimentalPagingApi::class)
    fun getChatList() = Pager(
        config = PagingConfig(CHAT_LIST_NETWORK_PAGE_SIZE),
        remoteMediator = ChatListRemoteMediator(api, localDb, getUserId())
    ) {
        chatListDao.getOrderByLatestMsg()
    }.flow

    //TODO: add presenter, he should map private or group chat to ui chat and format time
    suspend fun getChatById(id: Long, type: ChatType): Chat {
        val uiChat: Chat

        if (isOnline()) {
            when(type) {
                ChatType.PRIVATE -> {
                    val response = api.getPrivateChat(id, getUserId())
                    val otherPerson = response.otherPerson
                    personDao.insert(otherPerson)
                    uiChat = Chat(id = response.id, type = response.type,
                        name = otherPerson.getFullName(), description = otherPerson.onlineAt.toString())
                    chatDao.insert(Chat(id = response.id, type = response.type))
                }

                ChatType.GROUP -> {
                    uiChat = api.getGroupChat(id)
                    chatDao.insert(uiChat)
                }
            }
        } else {
            uiChat = chatDao.get(id)
        }

        return uiChat
    }
}
