package com.vladrip.ifchat.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class ChatListEl(
    @PrimaryKey val chatId: Long,
    val chatName: String,
    val chatType: Chat.ChatType,
    val lastMsgId: Long?,
    val lastMsgContent: String?,
    val lastMsgSentAt: LocalDateTime?,
    val isMuted: Boolean,
) {
    override fun toString(): String {
        return "chatId=$chatId"
    }
}