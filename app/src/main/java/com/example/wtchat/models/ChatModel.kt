package com.example.wtchat.models

data class ChatModel(
    val titulo: String = "",
    val descricao: String = "",
    val tipo: String = "",
    val participantes: List<String> = emptyList()
)
