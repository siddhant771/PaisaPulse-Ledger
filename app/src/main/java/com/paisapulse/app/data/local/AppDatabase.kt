package com.paisapulse.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.paisapulse.app.data.local.entities.BudgetEntity
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.data.local.dao.TransactionDao

@Database(
    entities = [TransactionEntity::class, BudgetEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE transactions ADD COLUMN isRecurring INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS budgets (
                        category TEXT NOT NULL PRIMARY KEY,
                        monthlyLimit REAL NOT NULL,
                        alertThreshold REAL NOT NULL DEFAULT 0.8
                    )
                    """.trimIndent()
                )
            }
        }
    }
}