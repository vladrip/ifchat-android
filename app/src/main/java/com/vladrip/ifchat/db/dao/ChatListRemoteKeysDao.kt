package com.vladrip.ifchat.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.model.ChatListRemoteKeys

@Dao
interface ChatListRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ChatListRemoteKeys>)

    @Query("SELECT * FROM ChatListRemoteKeys WHERE chatId = :chatId")
    suspend fun getByChatId(chatId: Long): ChatListRemoteKeys?

    @Query("DELETE FROM ChatListRemoteKeys")
    suspend fun clear()
}