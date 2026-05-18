package com.example.wtchat.api

import com.example.wtchat.models.ChatModel
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatsService {
    @GET("api/groups")
    suspend fun getChats(): List<ChatModel>
    @GET("api/groups/{segment}")
    suspend fun getChatsBySegment(@Path("segment") segment: String): List<ChatModel>
    @GET("api/groups/id/{chatId}")
    suspend fun getChatById(@Path("chatId") chatId: String): ChatModel
}