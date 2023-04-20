package com.vladrip.ifchat.model

import androidx.room.Embedded
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
    indices = [Index("chatId")]
)
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: Long,
    val sentAt: LocalDateTime,
    @Embedded(prefix = "sender_") val sender: Sender,
    val content: String
) {
    data class Sender(
        val id: Long,
        val firstName: String? = null,
        val lastName: String? = null
    ) {
        fun getFullName(): String {
            return "$firstName $lastName"
        }
    }
}