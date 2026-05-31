package com.paisapulse.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class PaisaPulseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}