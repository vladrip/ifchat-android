package com.vladrip.ifchat.data.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.data.entity.ChatMemberShort
import com.vladrip.ifchat.data.local.LocalDatabase
import com.vladrip.ifchat.data.network.IFChatService
import com.vladrip.ifchat.exception.ServerException
import com.vladrip.ifchat.exception.WaitingForNetworkException

@OptIn(ExperimentalPagingApi::class)
class ChatMembersRemoteMediator(
    private val api: IFChatService,
    private val localDb: LocalDatabase,
    private val chatId: Long,
) : RemoteMediator<Int, ChatMemberShort>() {
    private val chatMemberShortDao = localDb.chatMemberShortDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatMemberShort>,
    ): MediatorResult {
        Log.i("CHAT_MEMBERS_MEDIATOR", "chat$chatId $loadType, ${state.pages.lastOrNull()}")
        val nextPage: Int = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(true)
            LoadType.APPEND -> state.pages.size
        }

        Log.i("CHAT_MEMBERS_MEDIATOR", "Loading page $nextPage")
        return when (val response = api.getMembers(chatId, nextPage)) {
            is NetworkResponse.Success -> {
                val chatMembers = response.body.content
                Log.i("CHAT_MEMBERS_MEDIATOR", "response size: ${chatMembers.size}")
                localDb.withTransaction {
                    if (loadType == LoadType.REFRESH)
                        chatMemberShortDao.clear(chatId)
                    chatMemberShortDao.insertAll(chatMembers)
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