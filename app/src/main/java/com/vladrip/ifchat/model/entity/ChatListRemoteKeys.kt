package com.vladrip.ifchat.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatListRemoteKeys(
    @PrimaryKey(autoGenerate = false) val chatId: Long,
    val nextKey: Int?
)