package com.vladrip.ifchat.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.model.ChatListEl

@Dao
interface ChatListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chatListEl: List<ChatListEl>)

    @Query("SELECT * FROM ChatListEl ORDER BY lastMsgSentAt DESC")
    fun getOrderByLatestMsg(): PagingSource<Int, ChatListEl>

    @Query("SELECT lastMsgId FROM ChatListEl WHERE chatId = :chatId")
    fun getLastMsgIdByChatId(chatId: Long): Long

    @Query("DELETE FROM ChatListEl")
    suspend fun clear()
}