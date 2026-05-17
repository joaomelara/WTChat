package com.example.wtchat.api

import com.example.wtchat.models.UserModel
import retrofit2.http.GET
import retrofit2.http.Path

interface UsersService {
    @GET("api/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): UserModel
}