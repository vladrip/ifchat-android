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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
): ViewModel() {
    suspend fun getChatById(id: Long, type: Chat.ChatType): Chat {
        return chatRepository.getChatById(id, type)
    }

    fun getMessages(chatId: Long): Flow<PagingData<Message>> {
        return messageRepository.getMessagesByChatId(chatId).cachedIn(viewModelScope)
    }

    //TODO: complete formatting
    fun format(dateTimeStr: String?, context: Context): String {
        if (dateTimeStr == null) return context.getString(R.string.unknown)
        val dateTime = LocalDateTime.parse(dateTimeStr)
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }
}