package io.notable.ui_auth.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.notable.auth_interactors.Login
import io.notable.auth_interactors.Register
import io.notable.core.domain.DataState
import io.notable.core.domain.KQueue
import io.notable.core.domain.Logger
import io.notable.core.domain.UIComponent
import io.notable.shared.session.SessionEvents
import io.notable.shared.session.SessionManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val login: Login,
    private val register: Register,
    private val sessionManager: SessionManager,
    private val logger: Logger,
) : ViewModel() {

    val state: MutableState<AuthState> = mutableStateOf(AuthState())

    fun onTriggerEvent(event: AuthEvents) {
        when (event) {
            AuthEvents.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }
            AuthEvents.LoginPressed -> {
                handleLogin()
            }
            AuthEvents.RegisterPressed -> {
                handleRegister()
            }
        }
    }

    private fun handleLogin() {
        state.value.let { state ->
            login.execute(
                email = state.email.text,
                password = state.password.text,
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        this.state.value = state.copy(progressBarState = dataState.progressBarState)
                    }
                    is DataState.Data -> {
                        sessionManager.onTriggerEvent(SessionEvents.Login(dataState.data ?: ""))
                    }
                    is DataState.Response -> {
                        when (dataState.uiComponent) {
                            is UIComponent.Dialog,
                            is UIComponent.AreYouSureDialog,
                            is UIComponent.SnackBar -> {
                                appendToMessageQueue(dataState.uiComponent)
                            }

                            is UIComponent.None -> {
                                logger.log((dataState.uiComponent as UIComponent.None).message)
                            }
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun handleRegister() {
        state.value.let { state ->
            register.execute(
                email = state.email.text,
                password = state.password.text,
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        this.state.value = state.copy(progressBarState = dataState.progressBarState)
                    }
                    is DataState.Data -> {
                        sessionManager.onTriggerEvent(SessionEvents.Login(dataState.data ?: ""))
                    }
                    is DataState.Response -> {
                        when (dataState.uiComponent) {
                            is UIComponent.Dialog,
                            is UIComponent.AreYouSureDialog,
                            is UIComponent.SnackBar -> {
                                appendToMessageQueue(dataState.uiComponent)
                            }

                            is UIComponent.None -> {
                                logger.log((dataState.uiComponent as UIComponent.None).message)
                            }
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = KQueue(mutableListOf())) // force recompose
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove() // can throw exception if empty
            state.value = state.value.copy(errorQueue = KQueue(mutableListOf())) // force recompose
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            logger.log("Nothing to remove from DialogQueue")
        }
    }
}