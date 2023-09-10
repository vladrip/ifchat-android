package com.vladrip.ifchat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vladrip.ifchat.data.entity.Person

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: Person)

    @Query("SELECT * FROM Person WHERE uid = :uid")
    suspend fun get(uid: String): Person?
}