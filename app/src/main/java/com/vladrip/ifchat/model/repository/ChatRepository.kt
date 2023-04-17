package com.vladrip.ifchat.model.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.api.IFChatApi
import com.vladrip.ifchat.model.db.LocalDatabase
import com.vladrip.ifchat.model.entity.Chat
import com.vladrip.ifchat.ui.state.ChatUiState
import com.vladrip.ifchat.ui.state.StateHolder
import com.vladrip.ifchat.utils.FormatHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    fun getPrivateChatById(id: Long, context: Context): Flow<StateHolder<ChatUiState>> = flow {
        val localChat = chatDao.get(id)
        if (localChat != null)
            emit(StateHolder(state = ChatUiState(name = localChat.name)))

        val response = api.getPrivateChat(id, getUserId())
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

            is NetworkResponse.NetworkError -> StateHolder(status = StateHolder.Status.NETWORK_ERROR)
            else -> StateHolder(status = StateHolder.Status.ERROR)
        })
    }

    fun getGroupById(id: Long, context: Context): Flow<StateHolder<ChatUiState>> = flow {
        val localChat = chatDao.get(id)
        if (localChat != null)
            emit(
                StateHolder(
                    state = ChatUiState(
                        name = localChat.name,
                        shortInfo = context.getString(
                            R.string.group_members_count,
                            localChat.memberCount
                        )
                    )
                )
            )

        val response = api.getGroupChat(id)
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

            is NetworkResponse.NetworkError -> StateHolder(status = StateHolder.Status.NETWORK_ERROR)
            else -> StateHolder(status = StateHolder.Status.ERROR)
        })
    }
}
