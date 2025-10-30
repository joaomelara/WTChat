package com.example.wtchat.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wtchat.data.entity.LastAcessEntity


@Dao
interface LastAcessDao {

    @Query("SELECT * FROM chat_views WHERE chatId = :chatId LIMIT 1")
    suspend fun getLastAcess(chatId: String): LastAcessEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(chatView: LastAcessEntity)
}
