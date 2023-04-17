package com.vladrip.ifchat.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vladrip.ifchat.model.dao.ChatDao
import com.vladrip.ifchat.model.dao.ChatListDao
import com.vladrip.ifchat.model.dao.ChatListRemoteKeysDao
import com.vladrip.ifchat.model.dao.MessageDao
import com.vladrip.ifchat.model.dao.PersonDao
import com.vladrip.ifchat.model.entity.Chat
import com.vladrip.ifchat.model.entity.ChatListEl
import com.vladrip.ifchat.model.entity.ChatListRemoteKeys
import com.vladrip.ifchat.model.entity.ChatMember
import com.vladrip.ifchat.model.entity.ChatMemberShort
import com.vladrip.ifchat.model.entity.Message
import com.vladrip.ifchat.model.entity.Person

@TypeConverters(Converters::class)
@Database(
    entities = [Chat::class, ChatListEl::class, ChatListRemoteKeys::class,
        ChatMember::class, ChatMemberShort::class,
        Message::class,
        Person::class],
    version = 1
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun chatListDao(): ChatListDao
    abstract fun chatListRemoteKeysDao(): ChatListRemoteKeysDao
    abstract fun messageDao(): MessageDao
    abstract fun personDao(): PersonDao
}