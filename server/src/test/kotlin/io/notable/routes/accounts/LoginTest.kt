package io.notable.routes.accounts

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.constants.RouteConstants
import io.notable.dto.auth.AuthRequest
import io.notable.dto.auth.AuthResponse
import io.notable.dto.errors.Errors
import io.notable.utils.*
import junit.framework.TestCase
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LoginTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }

    private val loginRoute = "${RouteConstants.accountRoute}/${RouteConstants.loginRoute}"

    @Test
    fun whenRegistrationIsSuccessful_shouldBeAbleToLogin() =
        testWrapper {
            val client = getClient()

            val request = AuthRequest(getRandomEmail(), getRandomString(8))

            client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.let { response ->
                val body: AuthResponse = response.body()
                assertNotNull(body.token)
                TestCase.assertEquals(HttpStatusCode.Created, response.status)
            }

            client.post(loginRoute) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.let { response ->
                val body: AuthResponse = response.body()
                assertNotNull(body.token)
                TestCase.assertEquals(HttpStatusCode.Created, response.status)
            }
        }

    @Test
    fun whenNoUserIsFoundForTheGivenEmail_shouldReturnABadCredentialsException() =
        testWrapper {
            val client = getClient()

            val request = AuthRequest(getRandomEmail(), getRandomString(8))

            client.post(loginRoute) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.let { response ->
                val body: Errors = response.body()
                assertEquals(body.errors[0].field, "email")
                assertEquals(body.errors[0].message, "Invalid Email or Password")
                TestCase.assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }

    @Test
    fun whenInvalidCredentialsAreUsed_shouldReturnABadCredentialsException() =
        testWrapper {
            val client = getClient()

            val email = getRandomEmail()

            client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(email, getRandomString(8)))
            }.let { response ->
                val body: AuthResponse = response.body()
                assertNotNull(body.token)
                TestCase.assertEquals(HttpStatusCode.Created, response.status)
            }

            client.post(loginRoute) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(email, getRandomString(8)))
            }.let { response ->
                val body: Errors = response.body()
                assertEquals(body.errors[0].field, "email")
                assertEquals(body.errors[0].message, "Invalid Email or Password")
                assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }
}
