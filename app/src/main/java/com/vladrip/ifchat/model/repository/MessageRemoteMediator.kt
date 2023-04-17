package com.vladrip.ifchat.model.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.vladrip.ifchat.model.api.IFChatApi
import com.vladrip.ifchat.model.db.LocalDatabase
import com.vladrip.ifchat.model.entity.Message
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
    private val api: IFChatApi,
    private val localDb: LocalDatabase,
    private val chatId: Long
) : RemoteMediator<Int, Message>() {
    private val messageDao = localDb.messageDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Message>
    ): MediatorResult {
        Log.i("MESSAGE_REMOTE_MEDIATOR", "load($loadType, ${state.pages})")
        val loadKey: Long = when (loadType) {
            LoadType.REFRESH -> {
                val anchorMsgId =
                    state.anchorPosition?.let { state.closestItemToPosition(it)?.id ?: 0 }
                        ?: localDb.withTransaction {
                            localDb.chatListDao().getLastMsgIdByChatId(chatId)
                        }
                anchorMsgId
            }

            LoadType.PREPEND -> {
                val oldestSeenMsg = state.firstItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                oldestSeenMsg.id
            }

            LoadType.APPEND -> {
                val newestSeenMsg = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                newestSeenMsg.id
            }
        }

        try {
            Log.i("MESSAGE_REMOTE_MEDIATOR", "loadKey: $loadKey")
            val isRefresh = loadType == LoadType.REFRESH
            val messages = api.getMessages(
                chatId,
                afterId = if (loadType == LoadType.APPEND || isRefresh) loadKey else 0,
                beforeId = if (loadType == LoadType.PREPEND || isRefresh) loadKey else 0
            )

            localDb.withTransaction {
                messageDao.insertAll(messages)
            }
            return MediatorResult.Success(endOfPaginationReached = messages.isEmpty())
        } catch (e: IOException) {
            Log.e("MESSAGE_REMOTE_MEDIATOR", "IO: $e")
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("MESSAGE_REMOTE_MEDIATOR", "Http: $e")
            return MediatorResult.Error(e)
        }
    }
}