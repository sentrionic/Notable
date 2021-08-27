package io.notable.ui_notelist.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.notable.note_interactors.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteListModule {

    /**
     * @param interactors is provided in app module.
     */
    @Provides
    @Singleton
    fun provideSyncNotes(
        interactors: NoteInteractors
    ): SyncNotes {
        return interactors.syncNotes
    }

    @Provides
    @Singleton
    fun provideFilterNotes(
        interactors: NoteInteractors
    ): FilterNotes {
        return interactors.filterNotes
    }

    @Provides
    @Singleton
    fun provideSyncDeletedNotes(
        interactors: NoteInteractors
    ): SyncDeletedNotes {
        return interactors.syncDeletedNotes
    }

    @Provides
    @Singleton
    fun provideClearDatabase(
        interactors: NoteInteractors
    ): ClearDatabase {
        return interactors.clearDatabase
    }
}