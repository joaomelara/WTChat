package com.example.wtchat.api

import com.example.wtchat.models.AuthResponse
import com.example.wtchat.models.SignInRequest
import com.example.wtchat.models.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/signin")
    suspend fun signIn(@Body request: SignInRequest): AuthResponse

    @POST("api/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse
}

