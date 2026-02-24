package com.paisapulse.app.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase

import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.local.dao.TransactionDao


@Database(entities = [TransactionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
