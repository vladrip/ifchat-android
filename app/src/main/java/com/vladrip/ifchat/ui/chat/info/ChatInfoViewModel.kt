package com.vladrip.ifchat.ui.chat.info

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.vladrip.ifchat.R
import com.vladrip.ifchat.data.DataHolder
import com.vladrip.ifchat.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@SuppressLint("StaticFieldLeak") //because lint doesn't know APP context is injected and thinks there is a leak
@HiltViewModel
class ChatInfoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
) : ViewModel() {
    val chatId: Long = savedStateHandle["chatId"]!!
    val members = chatRepository.getMembers(chatId).cachedIn(viewModelScope)

    fun getChatInfo(): Flow<ChatInfoUiState> =
        chatRepository.getChat(chatId).map {
            when (it.status) {
                DataHolder.Status.SUCCESS -> ChatInfoUiState(it.data!!.description)
                DataHolder.Status.LOADING -> ChatInfoUiState(context.getString(R.string.loading))
                DataHolder.Status.NETWORK_ERROR -> ChatInfoUiState(context.getString(R.string.waiting_for_network))
                DataHolder.Status.ERROR -> ChatInfoUiState(context.getString(R.string.unknown_error))
            }
        }
}

data class ChatInfoUiState(
    val description: String? = null,
)