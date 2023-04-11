package com.vladrip.ifchat.model.api

import com.haroldadmin.cnradapter.NetworkResponse
import com.vladrip.ifchat.model.entity.Chat
import com.vladrip.ifchat.model.entity.Chat.ChatType
import com.vladrip.ifchat.model.entity.ChatListEl
import com.vladrip.ifchat.model.entity.Message
import com.vladrip.ifchat.model.entity.Person
import com.vladrip.ifchat.model.repository.CHAT_LIST_NETWORK_PAGE_SIZE
import com.vladrip.ifchat.model.repository.MESSAGE_NETWORK_PAGE_SIZE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IFChatApi {

    class ErrorResponse (
        val status: Int,
        val error: String
    )

    class ChatListResponse(
        val content: List<ChatListEl>
    )
    @GET("chats")
    suspend fun getChatList(
        @Query("personId") personId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int = CHAT_LIST_NETWORK_PAGE_SIZE
    ): ChatListResponse

    class PrivateChatResponse(
        val id: Long,
        val type: ChatType,
        val otherPerson: Person
    )
    @GET("chats/p{id}")
    suspend fun getPrivateChat(
        @Path("id") id: Long,
        @Query("personId") personId: Long
    ): NetworkResponse<PrivateChatResponse, ErrorResponse>

    @GET("chats/g{id}")
    suspend fun getGroupChat(
        @Path("id") id: Long
    ): NetworkResponse<Chat, ErrorResponse>

    @GET("chats/{id}/messages")
    suspend fun getMessages(
        @Path("id") chatId: Long,
        @Query("afterId") afterId: Long = 0,
        @Query("beforeId") beforeId: Long = 0,
        @Query("limit") limit: Int = MESSAGE_NETWORK_PAGE_SIZE
    ): List<Message>
}
/*
{
  "content": [
    {
      "chat": {
        "id": 3,
        "name": null,
        "description": null,
        "type": "PRIVATE",
        "publicGroup": false
      },
      "chatMember": {
        "id": 3,
        "chatId": 3,
        "personId": 1,
        "chatMuted": true
      },
      "lastMessage": {
        "id": 175,
        "fromNumber": "+86 611 231 1696",
        "content": "Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem. Integer tincidunt ante vel ipsum. Praesent blandit lacinia erat. Vestibulum sed magna at nunc commodo placerat.",
        "sentAt": "2022-02-25T01:28:28",
        "chatId": 3
      }
    },
    {
      "chat": {
        "id": 6,
        "name": "Test Group",
        "description": "Big description about this test group",
        "type": "GROUP",
        "publicGroup": true
      },
      "chatMember": {
        "id": 6,
        "chatId": 6,
        "personId": 1,
        "chatMuted": false
      },
      "lastMessage": {
        "id": 227,
        "fromNumber": "+1 475 869 2400",
        "content": "Quisque ut erat. Curabitur gravida nisi at nibh. In hac habitasse platea dictumst. Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem. Integer tincidunt ante vel ipsum.",
        "sentAt": "2022-02-09T16:38:33",
        "chatId": 6
      }
    },
    {
      "chat": {
        "id": 1,
        "name": null,
        "description": null,
        "type": "PRIVATE",
        "publicGroup": true
      },
      "chatMember": {
        "id": 1,
        "chatId": 1,
        "personId": 1,
        "chatMuted": false
      },
      "lastMessage": {
        "id": 273,
        "fromNumber": "+380 99 301 1338",
        "content": "Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis. Ut at dolor quis odio consequat varius. Integer ac leo. Pellentesque ultrices mattis odio.",
        "sentAt": "2022-02-17T22:52:53",
        "chatId": 1
      }
    }
  ],
  "pageable": {
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 20,
    "unpaged": false,
    "paged": true
  },
  "last": true,
  "totalElements": 3,
  "totalPages": 1,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "first": true,
  "numberOfElements": 3,
  "empty": false
}
 */