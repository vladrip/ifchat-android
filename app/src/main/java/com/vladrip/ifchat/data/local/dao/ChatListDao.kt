package com.vladrip.ifchat.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.data.entity.ChatListEl
import java.time.LocalDateTime

@Dao
interface ChatListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chatListEl: List<ChatListEl>)

    @Query("SELECT * FROM ChatListEl ORDER BY lastMsgSentAt DESC")
    fun getOrderByLatestMsg(): PagingSource<Int, ChatListEl>

    @Query("SELECT lastMsgId FROM ChatListEl WHERE chatId = :chatId")
    suspend fun getLastMsgId(chatId: Long): Long

    @Query("""
        UPDATE ChatListEl 
        SET lastMsgId = :msgId, lastMsgContent = :msgContent, lastMsgSentAt = :msgSentAt
        WHERE chatId = :chatId
    """)
    suspend fun updateLastMsg(chatId: Long,
                              msgId: Long, msgContent: String, msgSentAt: LocalDateTime)

    @Query("UPDATE ChatListEl SET isMuted = :isMuted WHERE chatId = :chatId")
    suspend fun updateIsMuted(chatId: Long, isMuted: Boolean)

    @Query("DELETE FROM ChatListEl")
    suspend fun clear()
}