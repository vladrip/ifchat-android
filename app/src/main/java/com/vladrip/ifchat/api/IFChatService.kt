package com.vladrip.ifchat.api

import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.data.CHAT_LIST_NETWORK_PAGE_SIZE
import com.vladrip.ifchat.data.MESSAGE_NETWORK_PAGE_SIZE
import com.vladrip.ifchat.model.Chat
import com.vladrip.ifchat.model.Chat.ChatType
import com.vladrip.ifchat.model.ChatListEl
import com.vladrip.ifchat.model.Message
import com.vladrip.ifchat.model.Person
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IFChatService {

    class ErrorResponse(
        val status: Int,
        val error: String,
    )

    class ChatListResponse(
        val content: List<ChatListEl>,
        val totalPages: Int,
    )

    @GET("chats")
    suspend fun getChatList(
        @Query("personId") personId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int = CHAT_LIST_NETWORK_PAGE_SIZE,
    ): NetworkResponse<ChatListResponse, ErrorResponse>

    class PrivateChatResponse(
        val id: Long,
        val type: ChatType,
        val otherPerson: Person,
    )

    @GET("chats/p{id}")
    suspend fun getPrivateChat(
        @Path("id") id: Long,
        @Query("personId") personId: Long,
    ): NetworkResponse<PrivateChatResponse, ErrorResponse>

    @GET("chats/g{id}")
    suspend fun getGroupChat(
        @Path("id") id: Long,
    ): NetworkResponse<Chat, ErrorResponse>

    @GET("chats/{id}/messages")
    suspend fun getMessages(
        @Path("id") chatId: Long,
        @Query("afterId") afterId: Long = 0,
        @Query("beforeId") beforeId: Long = 0,
        @Query("limit") limit: Int = MESSAGE_NETWORK_PAGE_SIZE,
    ): List<Message>

    @POST("messages")
    suspend fun saveMessage(
        @Body message: Message,
    ): NetworkResponse<Unit, ErrorResponse>

    @DELETE("messages/{id}")
    suspend fun deleteMessage(
        @Path("id") id: Long,
    ): NetworkResponse<Unit, ErrorResponse>
}