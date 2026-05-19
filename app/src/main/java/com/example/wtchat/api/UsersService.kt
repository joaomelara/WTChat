package com.example.wtchat.api

import com.example.wtchat.models.UserModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersService {
    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): UserModel

    @GET("api/users")
    suspend fun getAllUsers(): List<UserModel>

    @GET("api/users/search")
    suspend fun getUsersBySegment(@Query("segment") segment: String): List<UserModel>
}