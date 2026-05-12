package com.paisapulse.app.di

import android.content.Context
import androidx.room.Room
import com.paisapulse.app.data.local.AppDatabase
import com.paisapulse.app.data.local.AppDatabase.Companion.MIGRATION_3_4
import com.paisapulse.app.data.local.AppDatabase.Companion.MIGRATION_4_5

import com.paisapulse.app.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "paisa_pulse_db"
        ).addMigrations(MIGRATION_3_4, MIGRATION_4_5).build()
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
}