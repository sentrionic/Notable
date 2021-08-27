package io.notable.app.di

import android.app.Application
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.notable.note_interactors.DeleteNote
import io.notable.note_interactors.GetNoteFromCache
import io.notable.note_interactors.NoteInteractors
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteInteractorsModule {

    @Provides
    @Singleton
    @Named("noteAndroidSqlDriver")
    fun provideAndroidDriver(app: Application): SqlDriver {
        return AndroidSqliteDriver(
            schema = NoteInteractors.schema,
            context = app,
            name = NoteInteractors.dbName
        )
    }

    /**
     * Provide all the interactors in note-interactors module
     */
    @Provides
    @Singleton
    fun provideNoteInteractors(
        @Named("noteAndroidSqlDriver") sqlDriver: SqlDriver,
    ): NoteInteractors {
        return NoteInteractors.build(sqlDriver)
    }

    @Provides
    @Singleton
    fun provideGetNoteFromCache(
        interactors: NoteInteractors
    ): GetNoteFromCache {
        return interactors.getNoteFromCache
    }

    @Provides
    @Singleton
    fun provideDeleteNote(
        interactors: NoteInteractors
    ): DeleteNote {
        return interactors.deleteNote
    }
}