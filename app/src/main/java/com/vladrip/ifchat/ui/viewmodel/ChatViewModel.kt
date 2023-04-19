package com.vladrip.ifchat.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.vladrip.ifchat.R
import com.vladrip.ifchat.model.Chat
import com.vladrip.ifchat.model.Message
import com.vladrip.ifchat.data.ChatRepository
import com.vladrip.ifchat.data.MessageRepository
import com.vladrip.ifchat.ui.state.ChatUiState
import com.vladrip.ifchat.ui.state.StateHolder
import com.vladrip.ifchat.utils.FormatHelper
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

    fun getMessages(chatId: Long): Flow<PagingData<UiModel>> =
        messageRepository.getMessagesByChatId(chatId)
            .map { pagingData -> pagingData.map { UiModel.MessageItem(it) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) return@insertSeparators null
                    if (before == null) return@insertSeparators UiModel.DateSeparator(
                            FormatHelper.formatDateSeparator(after.message.sentAt)
                        )

                    if (after.message.sentAt.dayOfYear != before.message.sentAt.dayOfYear) {
                        UiModel.DateSeparator(FormatHelper.formatDateSeparator(after.message.sentAt))
                    } else null
                }
            }
}

sealed class UiModel {
    data class MessageItem(val message: Message) : UiModel()
    data class DateSeparator(val formattedDate: String) : UiModel()
}