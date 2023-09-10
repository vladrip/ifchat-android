package com.vladrip.ifchat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.data.entity.Chat

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: Chat)

    @Query("SELECT * FROM Chat c WHERE id = :id")
    suspend fun get(id: Long): Chat?
}