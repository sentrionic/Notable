package io.notable.ui_auth.ui

import io.notable.core.domain.KQueue
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.ui_auth.validation.EmailState
import io.notable.ui_auth.validation.PasswordState

data class AuthState(
    val email: EmailState = EmailState(),
    val password: PasswordState = PasswordState(),
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val errorQueue: KQueue<UIComponent> = KQueue(mutableListOf()),
)