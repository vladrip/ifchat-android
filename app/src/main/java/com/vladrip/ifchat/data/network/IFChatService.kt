package com.vladrip.ifchat.data.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.data.entity.ChatListEl
import com.vladrip.ifchat.data.entity.ChatMemberShort
import com.vladrip.ifchat.data.entity.Message
import com.vladrip.ifchat.data.entity.Person
import com.vladrip.ifchat.data.network.model.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

const val CHAT_LIST_NETWORK_PAGE_SIZE = 20
const val MESSAGE_NETWORK_PAGE_SIZE = 25
const val CHAT_MEMBERS_PAGE_SIZE = 30

interface IFChatService {

    @GET("chats")
    suspend fun getChatList(
        @Query("page") page: Int,
        @Query("size") size: Int = CHAT_LIST_NETWORK_PAGE_SIZE,
    ): NetworkResponse<PagedResponse<ChatListEl>, ErrorResponse>

    @GET("chats/{id}")
    suspend fun getChat(
        @Path("id") id: Long,
    ): NetworkResponse<ChatDto, ErrorResponse>

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
        @Path("uid") uid: String,
    ): NetworkResponse<Person, ErrorResponse>

    @GET("chats/{id}/members")
    suspend fun getMembers(
        @Path("id") chatId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int = CHAT_MEMBERS_PAGE_SIZE,
    ): NetworkResponse<PagedResponse<ChatMemberShort>, ErrorResponse>
}