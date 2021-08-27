package io.notable.note_interactors

import io.notable.constants.SuccessHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource.cache.NoteCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ClearDatabase(
    private val cache: NoteCache,
) {
    fun execute(
    ): Flow<DataState<Unit>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            cache.clearCache()

            emit(
                DataState.Response<Unit>(
                    uiComponent = UIComponent.None(
                        message = SuccessHandling.DATABASE_CLEARED
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Response<Unit>(
                    uiComponent = UIComponent.Dialog(
                        title = "Error",
                        description = e.message ?: "Unknown error"
                    )
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}