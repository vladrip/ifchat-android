package com.vladrip.ifchat

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.vladrip.ifchat.model.db.LocalDatabase
import com.vladrip.ifchat.model.entity.ChatListEl
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class EntityTest {
    private lateinit var db: LocalDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LocalDatabase::class.java
        ).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private val chatListJson =
        "{\"chat\":{\"id\":3,\"name\":null,\"description\":null,\"type\":\"PRIVATE\",\"publicGroup\":false},\"chatMember\":{\"id\":3,\"chatId\":3,\"personId\":1,\"chatMuted\":true},\"lastMessage\":{\"id\":175,\"fromNumber\":\"+866112311696\",\"content\":\"Aliquamauguequam,sollicitudinvitae,consectetuereget,rutrumat,lorem.Integertinciduntantevelipsum.Praesentblanditlaciniaerat.Vestibulumsedmagnaatnunccommodoplacerat.\",\"sentAt\":\"2022-02-25T01:28:28\",\"chatId\":3}}"

    @Test
    fun deserializeChatList() {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                LocalDateTime::class.java,
                JsonDeserializer { json, _, _ -> LocalDateTime.parse(json.asString) })
            .create()
        val chatListEl = gson.fromJson(chatListJson, ChatListEl::class.java)
        Log.e("deserializeChatList", gson.toJson(chatListEl))
    }
}