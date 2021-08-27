package io.notable.app.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.notable.core.domain.Logger
import io.notable.shared.session.AppDataStore
import io.notable.shared.session.AppDataStoreManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLogger(): Logger {
        return Logger(
            tag = "AppDebug",
            isDebug = true
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreManager(
        application: Application
    ): AppDataStore {
        return AppDataStoreManager(application)
    }

}