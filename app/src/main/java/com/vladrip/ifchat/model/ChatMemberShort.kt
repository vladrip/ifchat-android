package com.vladrip.ifchat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
class ChatMemberShort(
    @PrimaryKey val id: Long,
    val personUid: String,
    val firstName: String,
    val lastName: String,
    val onlineAt: LocalDateTime,
)