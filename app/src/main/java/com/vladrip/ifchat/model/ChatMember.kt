package com.vladrip.ifchat.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Chat::class,
        parentColumns = ["id"],
        childColumns = ["chatId"]
    ), ForeignKey(
        entity = Person::class,
        parentColumns = ["uid"],
        childColumns = ["personUid"]
    )],
    indices = [Index("chatId"), Index("personUid")]
)
data class ChatMember(
    @PrimaryKey val id: Long,
    val chatId: Long,
    val personUid: String,
    val chatMuted: Boolean = false,
)