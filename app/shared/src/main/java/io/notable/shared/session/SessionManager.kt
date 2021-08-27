package io.notable.shared.session

import androidx.lifecycle.MutableLiveData
import io.notable.constants.DataStoreKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * It keeps the authentication state of the user.
 */
@Singleton
class SessionManager
@Inject
constructor(
    private val appDataStoreManager: AppDataStore,
) {
    private val sessionScope = CoroutineScope(Main)

    val state: MutableLiveData<SessionState> = MutableLiveData(SessionState())

    init {
        // Check if a user was authenticated in a previous session
        sessionScope.launch {

            val token = appDataStoreManager.readValue(DataStoreKeys.AUTH_TOKEN)

            if (!token.isNullOrEmpty()) {
                state.value = state.value?.copy(authToken = token)
            }

            onFinishCheckingPrevAuthUser()
        }
    }

    fun onTriggerEvent(event: SessionEvents) {
        when (event) {
            is SessionEvents.Login -> {
                setAuthToken(event.token)
            }
            SessionEvents.Logout -> {
                logout()
            }
        }
    }

    private fun setAuthToken(token: String) {
        state.value = state.value?.copy(authToken = token)
        sessionScope.launch {
            appDataStoreManager.setValue(DataStoreKeys.AUTH_TOKEN, token)
        }
    }

    private fun onFinishCheckingPrevAuthUser() {
        state.value?.let { state ->
            this.state.value = state.copy(didCheckForPreviousAuthUser = true)
        }
    }

    private fun logout() {
        this.state.value = state.value?.copy(authToken = null)
        clearAuthUser()
        onFinishCheckingPrevAuthUser()
    }


    private fun clearAuthUser() {
        sessionScope.launch {
            appDataStoreManager.setValue(DataStoreKeys.AUTH_TOKEN, "")
        }
    }

}
