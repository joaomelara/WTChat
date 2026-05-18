package com.example.wtchat.api

import com.example.wtchat.models.MessageModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessagesService {
    @GET("api/messages/{chatId}")
    suspend fun getMessages(@Path("chatId") chatId: String): List<MessageModel>

    @POST("api/messages")
    suspend fun sendMessage(
        @Body message: MessageModel
    ): MessageModel
}