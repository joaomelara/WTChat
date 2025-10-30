package com.example.wtchat.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_views")
data class LastAcessEntity(
    @PrimaryKey val chatId: String,
    val lastSeen: Long
)
