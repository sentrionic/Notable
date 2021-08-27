package io.notable.auth_interactors

import io.notable.auth_datasource_test.network.AuthServiceFake
import io.notable.auth_datasource_test.network.AuthServiceResponseType
import io.notable.auth_datasource_test.network.data.AuthDataValid.token
import io.notable.constants.ErrorHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RegisterTest {
    // system in test
    private lateinit var register: Register
    private val email = "test@example.com"
    private val password = "password"

    @Test
    fun register_success() = runBlocking {
        // setup
        val authService = AuthServiceFake.build(
            type = AuthServiceResponseType.GoodData
        )

        register = Register(
            service = authService
        )

        // Execute the use-case
        val emissions = register.execute(email, password).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<String>(ProgressBarState.Loading))

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)
        assert((emissions[1] as DataState.Data).data == token)


        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<String>(ProgressBarState.Idle))
    }

    @Test
    fun register_emailAlreadyInUse() = runBlocking {
        // setup
        val authService = AuthServiceFake.build(
            type = AuthServiceResponseType.Forbidden
        )

        register = Register(
            service = authService
        )

        // Execute the use-case
        val emissions = register.execute(email, password).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<String>(ProgressBarState.Loading))

        // Confirm second emission is response
        assert(emissions[1] is DataState.Response)
        assert((emissions[1] as DataState.Response).uiComponent is UIComponent.Dialog)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == ErrorHandling.NETWORK_DATA_ERROR)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.EMAIL_ALREADY_IN_USE)

        assert(emissions[2] == DataState.Loading<String>(ProgressBarState.Idle))
    }

    @Test
    fun register_serverFailure() = runBlocking {
        // setup
        val authService = AuthServiceFake.build(
            type = AuthServiceResponseType.ServerError
        )

        register = Register(
            service = authService
        )

        // Execute the use-case
        val emissions = register.execute(email, password).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<String>(ProgressBarState.Loading))

        // Confirm second emission is response
        assert(emissions[1] is DataState.Response)
        assert((emissions[1] as DataState.Response).uiComponent is UIComponent.Dialog)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == ErrorHandling.NETWORK_DATA_ERROR)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.SERVER_ERROR)

        assert(emissions[2] == DataState.Loading<String>(ProgressBarState.Idle))
    }

    @Test
    fun createNote_wrongCredentials() = runBlocking {
        // setup
        val authService = AuthServiceFake.build(
            type = AuthServiceResponseType.Unauthorized
        )

        register = Register(
            service = authService
        )

        // Execute the use-case
        val emissions = register.execute(email, password).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<String>(ProgressBarState.Loading))

        // Confirm second emission is response
        assert(emissions[1] is DataState.Response)
        assert((emissions[1] as DataState.Response).uiComponent is UIComponent.Dialog)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == ErrorHandling.NETWORK_DATA_ERROR)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.INVALID_CREDENTIALS)

        assert(emissions[2] == DataState.Loading<String>(ProgressBarState.Idle))
    }
}