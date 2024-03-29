package com.vladrip.ifchat.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.data.entity.ChatListEl

@Dao
interface ChatListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chatListEl: List<ChatListEl>)

    @Query("SELECT * FROM ChatListEl ORDER BY lastMsgSentAt DESC")
    fun getOrderByLatestMsg(): PagingSource<Int, ChatListEl>

    @Query("SELECT lastMsgId FROM ChatListEl WHERE chatId = :chatId")
    suspend fun getLastMsgIdByChatId(chatId: Long): Long

    @Query("DELETE FROM ChatListEl")
    suspend fun clear()
}