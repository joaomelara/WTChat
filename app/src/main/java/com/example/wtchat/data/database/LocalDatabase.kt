package com.example.wtchat.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wtchat.data.dao.LastAcessDao
import com.example.wtchat.data.entity.LastAcessEntity

@Database(entities = [LastAcessEntity::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun lastAcessDao(): LastAcessDao
}
