package com.vladrip.ifchat.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
class ChatMemberShort(
    @PrimaryKey val id: Long,
    val personId: Long,
    val firstName: String,
    val lastName: String,
    val onlineAt: LocalDateTime
)