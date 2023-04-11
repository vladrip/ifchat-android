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
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
): ViewModel() {
    //TODO: flow or livedata
    suspend fun getChatById(id: Long, type: Chat.ChatType, context: Context): ChatUiState {
        val chatStateHolder = chatRepository.getChatById(id, type, context)
        return when(chatStateHolder.status) {
            StateHolder.Status.SUCCESS -> chatStateHolder.state!!
            StateHolder.Status.NETWORK_ERROR -> ChatUiState(context.getString(R.string.waiting_for_network))
            StateHolder.Status.LOADING -> ChatUiState(context.getString(R.string.loading))
            StateHolder.Status.ERROR -> ChatUiState(context.getString(R.string.unknown))
        }
    }

    fun getMessages(chatId: Long): Flow<PagingData<Message>> {
        return messageRepository.getMessagesByChatId(chatId).cachedIn(viewModelScope)
    }
}