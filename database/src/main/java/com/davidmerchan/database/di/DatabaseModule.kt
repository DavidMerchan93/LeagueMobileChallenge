package com.davidmerchan.database.di

import android.content.Context
import com.davidmerchan.database.storage.Security
import com.davidmerchan.database.storage.Storage
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
    fun provideSecurity(
        @ApplicationContext context: Context
    ): Security = Security(context)

    @Provides
    @Singleton
    fun provideStorage(
        security: Security,
        @ApplicationContext context: Context
    ): Storage = Storage(security = security, context = context)
}
