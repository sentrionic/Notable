package io.notable.core.domain

sealed class ProgressBarState {

    object Loading : ProgressBarState()

    object Idle : ProgressBarState()
}