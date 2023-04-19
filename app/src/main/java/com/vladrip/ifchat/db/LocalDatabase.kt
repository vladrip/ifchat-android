package com.vladrip.ifchat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vladrip.ifchat.db.dao.ChatDao
import com.vladrip.ifchat.db.dao.ChatListDao
import com.vladrip.ifchat.db.dao.ChatListRemoteKeysDao
import com.vladrip.ifchat.db.dao.MessageDao
import com.vladrip.ifchat.db.dao.PersonDao
import com.vladrip.ifchat.model.Chat
import com.vladrip.ifchat.model.ChatListEl
import com.vladrip.ifchat.model.ChatListRemoteKeys
import com.vladrip.ifchat.model.ChatMember
import com.vladrip.ifchat.model.ChatMemberShort
import com.vladrip.ifchat.model.Message
import com.vladrip.ifchat.model.Person

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