package com.vladrip.ifchat.model.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.api.IFChatApi
import com.vladrip.ifchat.model.db.LocalDatabase
import com.vladrip.ifchat.model.entity.Chat
import com.vladrip.ifchat.model.entity.Chat.ChatType
import com.vladrip.ifchat.ui.state.ChatUiState
import com.vladrip.ifchat.ui.state.StateHolder
import com.vladrip.ifchat.utils.FormatHelper
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

    suspend fun getChatById(id: Long, type: ChatType, context: Context): StateHolder<ChatUiState> {
        val response = when(type) {
            ChatType.PRIVATE -> api.getPrivateChat(id, getUserId())
            ChatType.GROUP -> api.getGroupChat(id)
        }
        return when(response) {
            is NetworkResponse.Success -> {
                return when(type) {
                    ChatType.PRIVATE -> {
                        val body = response.body as IFChatApi.PrivateChatResponse
                        val otherPerson = body.otherPerson
                        personDao.insert(otherPerson)
                        chatDao.insert(Chat(id = body.id, type = body.type))
                        StateHolder(state = ChatUiState(
                            otherPerson.getFullName(),
                            FormatHelper.formatLastOnline(otherPerson.onlineAt, context)
                        ))
                    }

                    ChatType.GROUP -> {
                        val chat = response.body as Chat
                        chatDao.insert(chat)
                        StateHolder(state = ChatUiState(
                            chat.name,
                            context.getString(R.string.group_members_count, chat.memberCount)
                        ))
                    }
                }
            }
            is NetworkResponse.NetworkError -> StateHolder(status = StateHolder.Status.NETWORK_ERROR)
            else -> StateHolder(status = StateHolder.Status.ERROR)
        }
    }
}
