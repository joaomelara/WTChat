package com.example.wtchat.api

import com.example.wtchat.models.ChatModel
import retrofit2.http.GET

interface ChatsService {
    @GET("api/groups")
    suspend fun getChats(): List<ChatModel>
}