package com.example.wtchat.data.repository

import com.example.wtchat.data.entity.LastAcessEntity
import com.example.wtchat.data.dao.LastAcessDao
import com.example.wtchat.data.database.LocalDatabase


class LastAcessRepository(private val db: LocalDatabase) {

    suspend fun updateLastSeen(chatId: String) {
        val now = System.currentTimeMillis()
        db.lastAcessDao().insertOrUpdate(LastAcessEntity(chatId, now))
    }

    suspend fun getLastSeen(chatId: String): Long {
        return db.lastAcessDao().getLastAcess(chatId)?.lastSeen ?: 0L
    }
}
