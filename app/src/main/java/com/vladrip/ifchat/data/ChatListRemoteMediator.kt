package com.vladrip.ifchat.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.vladrip.ifchat.api.IFChatApi
import com.vladrip.ifchat.db.LocalDatabase
import com.vladrip.ifchat.model.ChatListEl
import com.vladrip.ifchat.model.ChatListRemoteKeys
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ChatListRemoteMediator(
    private val api: IFChatApi,
    private val localDb: LocalDatabase,
    private val personId: Long
) : RemoteMediator<Int, ChatListEl>() {
    private val chatListDao = localDb.chatListDao()
    private val chatListRemoteKeysDao = localDb.chatListRemoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatListEl>
    ): MediatorResult {
        Log.i("CHAT_LIST_REMOTE_MEDIATOR", "load($loadType, ${state.pages})")
        val loadKey: Int = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(true)
            LoadType.APPEND -> {
                val remoteKeys =
                    state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
                        ?.let { chatListEl ->
                            chatListRemoteKeysDao.getByChatId(chatListEl.chatId)
                        }
                Log.i("CHAT_LIST_REMOTE_MEDIATOR", "remoteKeys: $remoteKeys")
                val nextKey =
                    remoteKeys?.nextKey ?: return MediatorResult.Success(remoteKeys != null)
                nextKey
            }
        }

        try {
            Log.i("CHAT_LIST_REMOTE_MEDIATOR", "loadKey: $loadKey")
            val response = api.getChatList(personId, loadKey, state.config.pageSize)
            val chatList = response.content
            val endOfPaginationReached = chatList.isEmpty()
            Log.i("CHAT_LIST_REMOTE_MEDIATOR", "response size: ${chatList.size}")
            Log.i("CHAT_LIST_REMOTE_MEDIATOR", "end: $endOfPaginationReached")

            localDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    chatListRemoteKeysDao.clear()
                    chatListDao.clear()
                }

                val nextKey = if (endOfPaginationReached) null else loadKey + 1
                val remoteKeys = chatList.map {
                    ChatListRemoteKeys(it.chatId, nextKey)
                }
                chatListRemoteKeysDao.insertAll(remoteKeys)
                chatListDao.insertAll(chatList)
            }
            return MediatorResult.Success(endOfPaginationReached)
        } catch (e: IOException) {
            Log.e("CHAT_LIST_REMOTE_MEDIATOR", "IO: $e")
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("CHAT_LIST_REMOTE_MEDIATOR", "Http: $e")
            return MediatorResult.Error(e)
        }
    }
}