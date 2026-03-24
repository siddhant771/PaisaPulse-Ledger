package com.paisapulse.app.di

import com.paisapulse.app.data.repository.TransactionRepositoryImpl
import com.paisapulse.app.domain.repository.ITransactionRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): ITransactionRepository
}