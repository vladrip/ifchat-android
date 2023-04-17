package com.vladrip.ifchat.ui

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.entity.Chat
import com.vladrip.ifchat.model.entity.Message
import com.vladrip.ifchat.model.repository.ChatRepository
import com.vladrip.ifchat.model.repository.MessageRepository
import com.vladrip.ifchat.ui.state.ChatUiState
import com.vladrip.ifchat.ui.state.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    fun getChatById(id: Long, type: Chat.ChatType, context: Context): Flow<ChatUiState> {
        val chatStateHolder = when (type) {
            Chat.ChatType.PRIVATE -> chatRepository.getPrivateChatById(id, context)
            Chat.ChatType.GROUP -> chatRepository.getGroupById(id, context)
        }
        return chatStateHolder.map {
            when (it.status) {
                StateHolder.Status.SUCCESS -> it.state!!
                StateHolder.Status.NETWORK_ERROR ->
                    ChatUiState(shortInfo = context.getString(R.string.waiting_for_network))

                StateHolder.Status.LOADING -> ChatUiState(shortInfo = context.getString(R.string.loading))
                StateHolder.Status.ERROR -> ChatUiState(shortInfo = context.getString(R.string.unknown))
            }
        }
    }

    fun getMessages(chatId: Long): Flow<PagingData<Message>> {
        return messageRepository.getMessagesByChatId(chatId).cachedIn(viewModelScope)
    }
}