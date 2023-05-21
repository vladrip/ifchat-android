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
    @PrimaryKey val id: Long = 0,
    val chatId: Long,
    val sentAt: LocalDateTime,
    @Embedded(prefix = "sender_") val sender: Sender,
    val content: String,
    val status: Status? = Status.READ,
) {
    data class Sender(
        val uid: String,
        val firstName: String? = null,
        val lastName: String? = null,
    ) {
        fun getFullName(): String {
            return "$firstName $lastName"
        }
    }

    enum class Status {
        SENDING,
        SENT,
        READ,
        DELETING
    }
}