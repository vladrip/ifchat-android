package com.vladrip.ifchat.model.repository

import com.vladrip.ifchat.model.api.IFChatApi
import com.vladrip.ifchat.model.db.LocalDatabase
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

open class BaseRepository(
    protected val api: IFChatApi,
    protected val localDb: LocalDatabase
) {
    fun isOnline(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    //TODO: query db there and pass real person id
    fun getUserId(): Long {
        return 1
    }
}