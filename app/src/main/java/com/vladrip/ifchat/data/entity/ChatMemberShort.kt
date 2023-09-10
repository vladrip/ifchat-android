package com.vladrip.ifchat.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
class ChatMemberShort(
    @PrimaryKey val id: Long,
    val chatId: Long,
    val firstName: String,
    val lastName: String,
    val onlineAt: LocalDateTime,
) {
    fun fullName(): String {
        return "$firstName $lastName"
    }
}