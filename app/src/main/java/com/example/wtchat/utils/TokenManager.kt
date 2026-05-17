package com.example.wtchat.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.wtchat.models.UserModel
import kotlinx.serialization.json.Json

class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    fun saveToken(accessToken: String, user: UserModel) {
        val userJson = json.encodeToString(UserModel.serializer(), user)
        sharedPreferences.edit().apply {
            putString("access_token", accessToken)
            putString("user_model", userJson)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)

    fun getUser(): UserModel? {
        val userJson = sharedPreferences.getString("user_model", null)
        return userJson?.let {
            try {
                json.decodeFromString(UserModel.serializer(), it)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun clearToken() {
        sharedPreferences.edit().apply {
            remove("access_token")
            remove("user_model")
            apply()
        }
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null
}