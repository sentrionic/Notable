package io.notable.ui_noteform.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.notable.note_interactors.CreateNote
import io.notable.note_interactors.NoteInteractors
import io.notable.note_interactors.UpdateNote
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteFormModule {

    @Provides
    @Singleton
    fun provideCreateNote(
        interactors: NoteInteractors
    ): CreateNote {
        return interactors.createNote
    }

    @Provides
    @Singleton
    fun provideUpdateNote(
        interactors: NoteInteractors
    ): UpdateNote {
        return interactors.updateNote
    }
}