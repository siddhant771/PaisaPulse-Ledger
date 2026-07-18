package com.paisapulse.app.di

import android.content.Context
import androidx.room.Room
import com.paisapulse.app.data.local.PaisaPulseDatabase
import com.paisapulse.app.data.local.TransactionDao
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
    fun providePaisaPulseDatabase(@ApplicationContext context: Context): PaisaPulseDatabase {
        return Room.databaseBuilder(
            context,
            PaisaPulseDatabase::class.java,
            "paisapulse_db"
        )
            .addMigrations(PaisaPulseDatabase.MIGRATION_2_3, PaisaPulseDatabase.MIGRATION_3_4)
            .build()
    }

    @Provides
    fun provideTransactionDao(database: PaisaPulseDatabase): TransactionDao {
        return database.transactionDao()
    }
}
