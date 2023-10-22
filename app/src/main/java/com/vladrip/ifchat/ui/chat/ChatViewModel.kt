package com.vladrip.ifchat.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vladrip.ifchat.R
import com.vladrip.ifchat.data.DataHolder.Status
import com.vladrip.ifchat.data.entity.Chat.ChatType
import com.vladrip.ifchat.data.entity.Message
import com.vladrip.ifchat.data.network.model.UserChatMemberDto
import com.vladrip.ifchat.data.repository.ChatRepository
import com.vladrip.ifchat.data.repository.MessageRepository
import com.vladrip.ifchat.data.repository.PersonRepository
import com.vladrip.ifchat.utils.FormatHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

@SuppressLint("StaticFieldLeak") //because lint doesn't know APP context is injected and thinks there is a leak
@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val personRepository: PersonRepository,
) : ViewModel() {
    val chatId: Long = savedStateHandle["chatId"]!!
    val chatType: ChatType = savedStateHandle["chatType"]!!
    val chat = chatRepository.getChat(chatId).map {
        it.data?.let { data -> Log.i("CHAT", it.status.name + data.userChatMember?.isChatMuted.toString()) }
        when (it.status) {
            Status.SUCCESS -> {
                val chat = it.data!!
                when (chat.type) {
                    ChatType.PRIVATE -> {
                        val otherPerson = personRepository.getPerson(chat.otherPersonUid!!)
                            .first { personHolder -> personHolder.status == Status.SUCCESS }.data
                        ChatUiState(
                            name = otherPerson?.fullName(),
                            shortInfo = otherPerson?.onlineAt.toString(),
                            userChatMember = chat.userChatMember
                        )
                    }

                    ChatType.GROUP -> ChatUiState(
                        name = chat.name,
                        shortInfo = context.getString(
                            R.string.group_members_count,
                            chat.memberCount
                        ),
                    )
                }
            }

            Status.LOADING -> ChatUiState(shortInfo = context.getString(R.string.loading))
            Status.NETWORK_ERROR -> ChatUiState(shortInfo = context.getString(R.string.waiting_for_network))
            Status.ERROR -> ChatUiState(shortInfo = context.getString(R.string.unknown_error))
        }
    }

    fun getMessages(): Flow<PagingData<UiModel>> = messageRepository.getMessagesByChatId(chatId)
        .map { pagingData -> pagingData.map { UiModel.MessageItem(it) } }.map {
            it.insertSeparators { before, after ->
                if (after == null) return@insertSeparators null
                else if (before == null || after.message.sentAt.dayOfYear != before.message.sentAt.dayOfYear) UiModel.DateSeparator(
                    FormatHelper.formatDateSeparator(after.message.sentAt)
                ) else null
            }
        }

    suspend fun sendMessage(text: String) {
        val uid = Firebase.auth.uid ?: return
        val message = Message(
            chatId = chatId,
            sentAt = LocalDateTime.now(),
            sender = Message.Sender(uid = uid),
            content = text,
            status = Message.Status.SENDING,
        )
        messageRepository.save(message, true)
    }

    suspend fun deleteMessage(id: Long) {
        messageRepository.delete(id)
    }

    suspend fun muteChat(value: Boolean): Boolean {
        return chatRepository.muteChat(chatId, value)
    }
}

sealed class UiModel {
    data class MessageItem(val message: Message) : UiModel()
    data class DateSeparator(val formattedDate: String) : UiModel()
}

data class ChatUiState(
    val name: String? = null,
    val shortInfo: String? = null,
    val userChatMember: UserChatMemberDto? = null
)