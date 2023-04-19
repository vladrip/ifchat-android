package com.vladrip.ifchat.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey val id: Long,
    val type: ChatType = ChatType.PRIVATE,
    val name: String? = "",
    val description: String? = "",
    val memberCount: Int = 2
) {
    enum class ChatType {
        PRIVATE,
        GROUP
    }
}