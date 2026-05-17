package com.example.wtchat.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: String = "",
    val username: String = "",
    val name: String = "",
    val email: String = "",
    val roles: List<String> = emptyList(),
    val segment: String = ""
)
