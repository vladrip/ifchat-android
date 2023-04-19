package com.vladrip.ifchat.data

import com.vladrip.ifchat.api.IFChatApi
import com.vladrip.ifchat.db.LocalDatabase
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

open class BaseRepository(
    protected val api: IFChatApi,
    protected val localDb: LocalDatabase
) {
    //TODO: query db there and pass real person id
    fun getUserId(): Long {
        return 1
    }
}