package com.example.wtchat.models

data class UserModel(
    val uid: String = "",
    val crm: String = "",
    val nome: String = "",
    val email: String = "",
    val roles: List<String> = emptyList(),
    val segment: String = ""
)
