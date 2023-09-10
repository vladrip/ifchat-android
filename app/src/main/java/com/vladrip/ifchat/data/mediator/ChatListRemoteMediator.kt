package com.vladrip.ifchat.data.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.data.entity.ChatListEl
import com.vladrip.ifchat.data.local.LocalDatabase
import com.vladrip.ifchat.data.network.IFChatService
import com.vladrip.ifchat.exception.ServerException
import com.vladrip.ifchat.exception.WaitingForNetworkException

@OptIn(ExperimentalPagingApi::class)
class ChatListRemoteMediator(
    private val api: IFChatService,
    private val localDb: LocalDatabase,
) : RemoteMediator<Int, ChatListEl>() {
    private val chatListDao = localDb.chatListDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatListEl>,
    ): MediatorResult {
        Log.i("CHAT_LIST_MEDIATOR", "$loadType, ${state.pages.lastOrNull()}")
        val nextPage: Int = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(true)
            LoadType.APPEND -> state.pages.size
        }

        Log.i("CHAT_LIST_MEDIATOR", "Loading page $nextPage")
        return when (val response = api.getChatList(nextPage)) {
            is NetworkResponse.Success -> {
                val chatList = response.body.content
                Log.i("CHAT_MEMBERS_MEDIATOR", "response size: ${chatList.size}")
                localDb.withTransaction {
                    if (loadType == LoadType.REFRESH)
                        chatListDao.clear()
                    chatListDao.insertAll(chatList)
                }
                MediatorResult.Success(response.body.last)
            }

            is NetworkResponse.NetworkError -> MediatorResult.Error(WaitingForNetworkException())
            is NetworkResponse.ServerError -> MediatorResult.Error(
                ServerException("${response.body?.status}: ${response.body?.error}")
            )

            is NetworkResponse.UnknownError -> MediatorResult.Error(response.error)
        }
    }
}