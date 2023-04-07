package com.vladrip.ifchat.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Chat(
    @PrimaryKey val id: Long,
    val type: ChatType = ChatType.PRIVATE,
    val name: String? = null,
    val description: String? = null,
    val memberCount: Int? = null
) {
    enum class ChatType {
        PRIVATE,
        GROUP
    }
}