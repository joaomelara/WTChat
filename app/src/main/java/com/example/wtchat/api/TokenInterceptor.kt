package com.example.wtchat.api

import android.content.Context
import com.example.wtchat.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenManager = TokenManager(context)
        val token = tokenManager.getAccessToken()

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // If token exists and is not already a Bearer token, add it
        if (!token.isNullOrEmpty()) {
            // The token already includes "Bearer " prefix from the backend
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

