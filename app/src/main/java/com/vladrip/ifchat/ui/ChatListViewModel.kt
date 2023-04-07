package com.vladrip.ifchat.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vladrip.ifchat.model.entity.ChatListEl
import com.vladrip.ifchat.model.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun getChatList(): Flow<PagingData<ChatListEl>> {
        return repository.getChatList().cachedIn(viewModelScope)
    }
}