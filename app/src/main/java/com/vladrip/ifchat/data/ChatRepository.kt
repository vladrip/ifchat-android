package com.vladrip.ifchat.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.executeWithRetry
import com.vladrip.ifchat.R
import com.vladrip.ifchat.api.CHAT_LIST_NETWORK_PAGE_SIZE
import com.vladrip.ifchat.api.IFChatService
import com.vladrip.ifchat.db.LocalDatabase
import com.vladrip.ifchat.model.Chat
import com.vladrip.ifchat.ui.state.ChatUiState
import com.vladrip.ifchat.ui.state.StateHolder
import com.vladrip.ifchat.utils.FormatHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    @OptIn(ExperimentalPagingApi::class)
    fun getChatList() = Pager(
        config = PagingConfig(CHAT_LIST_NETWORK_PAGE_SIZE),
        remoteMediator = ChatListRemoteMediator(api, localDb)
    ) {
        chatListDao.getOrderByLatestMsg()
    }.flow

    fun getPrivateById(id: Long, context: Context): Flow<StateHolder<ChatUiState>> = flow {
        val localChat = chatDao.get(id)
        emit(
            if (localChat != null)
                StateHolder(state = ChatUiState(name = localChat.name))
            else StateHolder(status = StateHolder.Status.LOADING)
        )

        var response = api.getPrivateChat(id)
        if (response is NetworkResponse.NetworkError) {
            emit(StateHolder(status = StateHolder.Status.NETWORK_ERROR))
            response = executeWithRetry(times = Int.MAX_VALUE) { api.getPrivateChat(id) }
        }

        emit(when (response) {
            is NetworkResponse.Success -> {
                val body = response.body
                val otherPerson = body.otherPerson
                val fullName = otherPerson.getFullName()
                localDb.withTransaction {
                    personDao.insert(otherPerson)
                    chatDao.insert(Chat(id = body.id, type = body.type, name = fullName))
                }
                StateHolder(
                    state = ChatUiState(
                        name = fullName,
                        shortInfo = FormatHelper.formatLastOnline(otherPerson.onlineAt, context)
                    )
                )
            }

            else -> StateHolder(status = StateHolder.Status.ERROR)
        })
    }

    fun getGroupById(id: Long, context: Context): Flow<StateHolder<ChatUiState>> = flow {
        val localChat = chatDao.get(id)
        emit(
            if (localChat != null)
                StateHolder(
                    state = ChatUiState(
                        name = localChat.name,
                        shortInfo = context.getString(
                            R.string.group_members_count,
                            localChat.memberCount
                        )
                    )
                )
            else StateHolder(status = StateHolder.Status.LOADING)
        )

        var response = api.getGroupChat(id)
        if (response is NetworkResponse.NetworkError) {
            emit(StateHolder(status = StateHolder.Status.NETWORK_ERROR))
            response = executeWithRetry(times = Int.MAX_VALUE) { api.getGroupChat(id) }
        }

        emit(when (response) {
            is NetworkResponse.Success -> {
                val chat = response.body
                localDb.withTransaction { chatDao.insert(chat) }
                StateHolder(
                    state = ChatUiState(
                        name = chat.name,
                        shortInfo = context.getString(
                            R.string.group_members_count,
                            chat.memberCount
                        )
                    )
                )
            }

            else -> StateHolder(status = StateHolder.Status.ERROR)
        })
    }
}
