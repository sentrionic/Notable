package io.notable.auth_interactors

import io.notable.auth_datasource.network.AuthService
import io.notable.auth_datasource.network.model.AuthInput
import io.notable.constants.ErrorHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Register(
    private val service: AuthService,
) {
    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<String>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            val token: String? = try {
                service.register(AuthInput(email, password))
            } catch (e: Exception) {
                e.printStackTrace()

                val message = AuthService.getErrorMessage(e)

                emit(
                    DataState.Response(
                        uiComponent = UIComponent.Dialog(
                            title = ErrorHandling.NETWORK_DATA_ERROR,
                            description = message
                        )
                    )
                )
                null
            }

            if (token != null) {
                emit(DataState.Data(token))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Response<String>(
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