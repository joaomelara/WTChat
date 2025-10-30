package com.example.wtchat.models

import java.util.Date

data class MessageModel(
    val autor: String = "",
    val nome: String = "",
    val texto: String = "",
    val data: Date? = null
)
