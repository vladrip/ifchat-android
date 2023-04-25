package com.vladrip.ifchat.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.haroldadmin.cnradapter.NetworkResponse
import com.haroldadmin.cnradapter.executeWithRetry
import com.vladrip.ifchat.api.IFChatService
import com.vladrip.ifchat.db.LocalDatabase
import com.vladrip.ifchat.model.ChatListEl
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ChatListRemoteMediator(
    private val api: IFChatService,
    localDb: LocalDatabase,
    private val personId: Long,
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

        try {
            Log.i("CHAT_LIST_MEDIATOR", "totalPages: $totalPages")
            Log.i("CHAT_LIST_MEDIATOR", "page: $nextPage")
            val response = executeWithRetry(
                times = Int.MAX_VALUE,
                initialDelay = 200
            ) {
                api.getChatList(personId, nextPage)
            }

            if (response is NetworkResponse.Success) {
                val chatList = response.body.content
                totalPages = response.body.totalPages
                Log.i("CHAT_LIST_MEDIATOR", "response size: ${chatList.size}")
                chatListDao.insertAll(chatList)
            } else if (response is NetworkResponse.Error)
                throw response.error ?: throw IOException("Unexpected error. NetworkError even after executeWithRetry?")
            return MediatorResult.Success(nextPage + 1 == totalPages)
        } catch (e: IOException) {
            Log.e("CHAT_REMOTE_MEDIATOR", "IO: $e")
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("CHAT_REMOTE_MEDIATOR", "Http: $e")
            return MediatorResult.Error(e)
        }
    }
}