package com.paisapulse.app.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TransactionEntity::class], version = 5)
abstract class PaisaPulseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE transactions_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        amount REAL NOT NULL,
                        description TEXT NOT NULL DEFAULT '',
                        category TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        isExpense INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT INTO transactions_new (id, amount, description, category, date, isExpense)
                    SELECT id, amount, merchant, category, timestamp, 1 FROM transactions
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE transactions")
                database.execSQL("ALTER TABLE transactions_new RENAME TO transactions")
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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE transactions_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        amount REAL NOT NULL,
                        description TEXT NOT NULL DEFAULT '',
                        category TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        isExpense INTEGER NOT NULL,
                        source TEXT NOT NULL DEFAULT 'manual',
                        smsFingerprint TEXT,
                        aiConfidence REAL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT INTO transactions_new
                    (id, amount, description, category, date, isExpense, source, smsFingerprint, aiConfidence)
                    SELECT id, amount, description, category, date, isExpense,
                    source, smsFingerprint, aiConfidence FROM transactions
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE transactions")
                database.execSQL("ALTER TABLE transactions_new RENAME TO transactions")
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_transactions_smsFingerprint " +
                        "ON transactions (smsFingerprint)"
                )
            }
        }
    }
}
