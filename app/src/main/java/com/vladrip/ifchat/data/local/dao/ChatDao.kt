package com.vladrip.ifchat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.data.entity.Chat
import com.vladrip.ifchat.data.network.model.UserChatMemberDto
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: Chat)

    @Query("SELECT * FROM Chat c WHERE id = :id")
    fun get(id: Long): Flow<Chat>

    @Query("UPDATE Chat SET userChatMember = :userChatMember WHERE id = :id")
    suspend fun updateUserChatMember(id: Long, userChatMember: UserChatMemberDto)
}