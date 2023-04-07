package com.vladrip.ifchat.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    foreignKeys = [ForeignKey(
        entity = Chat::class,
        parentColumns = ["id"],
        childColumns = ["chatId"]
    )],
    indices = [Index("chatId"), Index("fromNumber")]
)
data class Message(
    @PrimaryKey val id: Long,
    val fromNumber: String,
    val content: String,
    val sentAt: LocalDateTime,
    val chatId: Long
)