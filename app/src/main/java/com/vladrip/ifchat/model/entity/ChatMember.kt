package com.vladrip.ifchat.model.entity

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
        parentColumns = ["id"],
        childColumns = ["personId"]
    )],
    indices = [Index("chatId"), Index("personId")]
)
data class ChatMember(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val chatMuted: Boolean,
    val chatId: Long,
    val personId: Long
)