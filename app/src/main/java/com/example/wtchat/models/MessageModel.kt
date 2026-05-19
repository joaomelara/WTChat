package com.example.wtchat.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    val id: String = "",
    val author: String = "",
    val username: String = "",
    val message: String = "",
    val groupId: String = "",
    val timestamp: String = ""
)

