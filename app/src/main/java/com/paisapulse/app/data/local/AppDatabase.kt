package com.paisapulse.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.data.local.dao.TransactionDao


@Database(entities = [TransactionEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
