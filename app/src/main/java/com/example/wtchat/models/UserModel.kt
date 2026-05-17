package com.example.wtchat.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val uid: String = "",
    val crm: String = "",
    val nome: String = "",
    val email: String = "",
    val roles: List<String> = emptyList(),
    val segment: String = ""
)
