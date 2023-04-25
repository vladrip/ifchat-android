package com.vladrip.ifchat.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.model.Message

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Query("SELECT * FROM Message WHERE chatId = :chatId ORDER BY id asc")
    fun getAll(chatId: Long): PagingSource<Int, Message>

    @Query("SELECT * FROM Message WHERE id = :id")
    fun get(id: Long): Message

    @Query("SELECT * FROM Message WHERE status IN (:statuses)")
    suspend fun getFilteredByStatuses(statuses: List<String>): List<Message>

    @Query("SELECT max(id) FROM Message")
    fun getMaxId(): Long

    @Query("DELETE FROM Message WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM Message WHERE chatId = :chatId")
    suspend fun clear(chatId: Long)
}