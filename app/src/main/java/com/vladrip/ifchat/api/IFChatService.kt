package com.vladrip.ifchat.api

import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.model.Chat
import com.vladrip.ifchat.model.Chat.ChatType
import com.vladrip.ifchat.model.ChatListEl
import com.vladrip.ifchat.model.Message
import com.vladrip.ifchat.model.Person
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

const val CHAT_LIST_NETWORK_PAGE_SIZE = 20
const val MESSAGE_NETWORK_PAGE_SIZE = 25

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
        @Path("id") id: Long
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

    @POST("devices")
    suspend fun saveDeviceToken(
        @Body requestBody: RequestBody,
    ): NetworkResponse<Unit, ErrorResponse>

    @DELETE("devices/{deviceToken}")
    suspend fun deleteDeviceToken(
        @Path("deviceToken") deviceToken: String,
    ): NetworkResponse<Unit, ErrorResponse>

    @GET("persons/{uid}")
    suspend fun getPerson(
        @Path("uid") uid: String
    ): NetworkResponse<Person, ErrorResponse>
}