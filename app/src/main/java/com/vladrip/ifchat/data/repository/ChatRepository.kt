package com.vladrip.ifchat.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.executeWithRetry
import com.vladrip.ifchat.data.DataHolder
import com.vladrip.ifchat.data.entity.Chat
import com.vladrip.ifchat.data.local.LocalDatabase
import com.vladrip.ifchat.data.mediator.ChatListRemoteMediator
import com.vladrip.ifchat.data.mediator.ChatMembersRemoteMediator
import com.vladrip.ifchat.data.network.CHAT_LIST_NETWORK_PAGE_SIZE
import com.vladrip.ifchat.data.network.CHAT_MEMBERS_PAGE_SIZE
import com.vladrip.ifchat.data.network.IFChatService
import com.vladrip.ifchat.data.network.model.BooleanWrapper
import com.vladrip.ifchat.utils.Mapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val api: IFChatService,
    private val localDb: LocalDatabase,
) {
    private val chatDao = localDb.chatDao()
    private val chatListDao = localDb.chatListDao()
    private val personDao = localDb.personDao()
    private val chatMemberShortDao = localDb.chatMemberShortDao()

    @OptIn(ExperimentalPagingApi::class)
    fun getChatList() = Pager(
        config = PagingConfig(CHAT_LIST_NETWORK_PAGE_SIZE),
        remoteMediator = ChatListRemoteMediator(api, localDb)
    ) {
        chatListDao.getOrderByLatestMsg()
    }.flow

    fun getChat(id: Long): Flow<DataHolder<Chat>> =
        chatDao.get(id).map { chat -> DataHolder(chat) }
            .onStart {
                emit(DataHolder(status = DataHolder.Status.LOADING))
                var response = api.getChat(id)
                if (response is NetworkResponse.NetworkError) {
                    emit(DataHolder(status = DataHolder.Status.NETWORK_ERROR))
                    response = executeWithRetry(times = Int.MAX_VALUE) { api.getChat(id) }
                }

                emit(when (response) {
                    is NetworkResponse.Success -> {
                        val chatDto = response.body
                        val chat = Mapper.toChat(chatDto)
                        localDb.withTransaction {
                            if (chat.type == Chat.ChatType.PRIVATE)
                                personDao.insert(chatDto.otherPerson!!)
                            chatDao.insert(chat)
                        }
                        DataHolder(chat)
                    }

                    else -> DataHolder(status = DataHolder.Status.ERROR)
                })
            }

    @OptIn(ExperimentalPagingApi::class)
    fun getMembers(id: Long) = Pager(
        config = PagingConfig(CHAT_MEMBERS_PAGE_SIZE),
        remoteMediator = ChatMembersRemoteMediator(api, localDb, id),
    ) {
        chatMemberShortDao.getOrderByMostRecentOnline(id)
    }.flow

    suspend fun muteChat(chatId: Long, value: Boolean): Boolean {
        val response = executeWithRetry(
            times = Int.MAX_VALUE,
            initialDelay = 500,
        ) { api.setIsChatMuted(Firebase.auth.currentUser!!.uid, chatId, BooleanWrapper(value)) }

        return if (response is NetworkResponse.Success) {
            chatDao.updateUserChatMember(chatId, response.body)
            chatListDao.updateIsMuted(chatId, response.body.isChatMuted)
            true
        } else false
    }
}
