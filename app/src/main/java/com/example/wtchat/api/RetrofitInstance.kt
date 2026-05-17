package com.example.wtchat.api

import android.content.Context
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RetrofitInstance private constructor(context: Context) {
    companion object {

        private const val BASE_URL = "http://192.168.x.x:8080/"  // Para emulador: 10.0.2.2, para device: use IP real
        private var instance: RetrofitInstance? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = RetrofitInstance(context)
            }
        }

        fun getInstance(): RetrofitInstance {
            return instance ?: throw IllegalStateException("RetrofitInstance must be initialized first. Call initialize(context) in your Application or MainActivity.")
        }
    }



    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val tokenInterceptor = TokenInterceptor(context)

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(tokenInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val messagesService: MessagesService = retrofit.create(MessagesService::class.java)
    val chatsService: ChatsService = retrofit.create(ChatsService::class.java)
    val usersService: UsersService = retrofit.create(UsersService::class.java)
}

