package com.vladrip.ifchat.ui.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.vladrip.ifchat.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    chatRepository: ChatRepository,
) : ViewModel() {
    val chatList = chatRepository.getChatList().cachedIn(viewModelScope)
}