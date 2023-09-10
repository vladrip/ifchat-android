package com.vladrip.ifchat.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey val id: Long,
    val type: ChatType = ChatType.PRIVATE,
    val otherPersonUid: String?,
    val name: String? = "",
    val description: String? = "",
    val memberCount: Int = 2,
) {
    enum class ChatType {
        PRIVATE,
        GROUP,
    }
}
