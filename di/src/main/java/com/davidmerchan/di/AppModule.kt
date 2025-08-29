package com.davidmerchan.di

import com.davidmerchan.data.di.DataModule
import com.davidmerchan.database.di.DatabaseModule
import com.davidmerchan.domain.di.DomainModule
import com.davidmerchan.network.di.NetworkModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(
    includes = [
        NetworkModule::class,
        DatabaseModule::class,
        DataModule::class,
        DomainModule::class,
    ],
)
@InstallIn(SingletonComponent::class)
object AppModule
