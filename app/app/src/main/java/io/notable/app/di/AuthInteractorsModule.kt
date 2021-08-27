package io.notable.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.notable.auth_interactors.AuthInteractors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthInteractorsModule {
    /**
     * Provide all the interactors in auth-interactors module
     */
    @Provides
    @Singleton
    fun provideAuthInteractors(
    ): AuthInteractors {
        return AuthInteractors.build()
    }

}