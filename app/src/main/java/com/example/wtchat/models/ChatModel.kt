package com.example.wtchat.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatModel(
    val id: String = "",
    val name: String = "",
    val segment: String = "",
    val privateChatMembers: List<String> = emptyList()
)
