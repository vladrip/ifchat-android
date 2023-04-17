package com.vladrip.ifchat.model.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.model.entity.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Query("SELECT * FROM Message WHERE chatId = :chatId ORDER BY id desc")
    fun getMessages(chatId: Long): PagingSource<Int, Message>

    @Query("DELETE FROM Message WHERE chatId = :chatId")
    fun clear(chatId: Long)
}