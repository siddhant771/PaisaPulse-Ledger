package com.paisapulse.app.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TransactionEntity::class], version = 4)
abstract class PaisaPulseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transactions ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transactions ADD COLUMN source TEXT NOT NULL DEFAULT 'manual'")
                database.execSQL("ALTER TABLE transactions ADD COLUMN smsFingerprint TEXT")
                database.execSQL("ALTER TABLE transactions ADD COLUMN aiConfidence REAL")
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_transactions_smsFingerprint ON transactions (smsFingerprint)")
            }
        }
    }
}
