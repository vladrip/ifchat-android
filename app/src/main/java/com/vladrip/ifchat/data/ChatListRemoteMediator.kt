package com.vladrip.ifchat.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.api.IFChatService
import com.vladrip.ifchat.db.LocalDatabase
import com.vladrip.ifchat.exception.ServerException
import com.vladrip.ifchat.exception.WaitingForNetworkException
import com.vladrip.ifchat.model.ChatListEl

@OptIn(ExperimentalPagingApi::class)
class ChatListRemoteMediator(
    private val api: IFChatService,
    localDb: LocalDatabase
) : RemoteMediator<Int, ChatListEl>() {
    private val chatListDao = localDb.chatListDao()
    private var totalPages: Int = 1

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatListEl>,
    ): MediatorResult {
        Log.i("CHAT_LIST_MEDIATOR", "load($loadType, ${state.pages.lastOrNull()})")
        val nextPage: Int = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(true)
            LoadType.APPEND -> {
                val prevPage = state.pages.lastIndex
                prevPage + 1
            }
        }
        if (nextPage == totalPages) return MediatorResult.Success(true)

        Log.i("CHAT_LIST_MEDIATOR", "totalPages: $totalPages")
        Log.i("CHAT_LIST_MEDIATOR", "page: $nextPage")
        return when (val response = api.getChatList(nextPage)) {
            is NetworkResponse.Success -> {
                val chatList = response.body.content
                totalPages = response.body.totalPages
                Log.i("CHAT_LIST_MEDIATOR", "response size: ${chatList.size}")
                chatListDao.insertAll(chatList)

                MediatorResult.Success(nextPage + 1 == totalPages)
            }
            is NetworkResponse.NetworkError -> MediatorResult.Error(WaitingForNetworkException())
            is NetworkResponse.ServerError -> MediatorResult.Error(
                ServerException("${response.body?.status}: ${response.body?.error}")
            )
            is NetworkResponse.UnknownError -> MediatorResult.Error(response.error)
        }
    }
}