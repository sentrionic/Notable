package io.notable.ui_auth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.notable.auth_interactors.AuthInteractors
import io.notable.auth_interactors.Login
import io.notable.auth_interactors.Register
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideLogin(
        interactors: AuthInteractors
    ): Login {
        return interactors.login
    }

    @Provides
    @Singleton
    fun provideRegister(
        interactors: AuthInteractors
    ): Register {
        return interactors.register
    }
}