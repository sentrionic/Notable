package io.notable.routes.accounts

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.notable.constants.RouteConstants
import io.notable.dto.auth.AuthRequest
import io.notable.dto.auth.AuthResponse
import io.notable.dto.errors.ErrorDto
import io.notable.dto.errors.Errors
import io.notable.utils.*
import junit.framework.TestCase
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RegisterTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun whenRegistrationIsSuccessful_itShouldReturnAnAuthenticationToken() =
        testWrapper {
            val client = getClient()

            val response = client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(getRandomEmail(), getRandomString(8)))
            }

            val body: AuthResponse = response.body()
            assertNotNull(body.token)
            TestCase.assertEquals(HttpStatusCode.Created, response.status)
        }

    @Test
    fun whenCredentialsAreIllegal_shouldRespondWithBadRequestAndAListOfErrors() =
        testWrapper {
            val client = getClient()

            client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("", getRandomString(8)))
            }.let { response ->
                val body: Errors = response.body()
                assertEquals(body.errors[0].field, "email")
                assertEquals(body.errors[0].message, "\"email\" must not be empty")
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }

            client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody("{ \"password\": \"password\"  }")
            }.let { response ->
                val body: ErrorDto = response.body()
                assertEquals(body.message, "Illegal input")
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }

            client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(getRandomEmail(), ""))
            }.let { response ->
                val body: Errors = response.body()
                assertEquals(body.errors[0].field, "password")
                assertEquals(
                    body.errors[0].message,
                    "The size of \"password\" must be greater than or equal to 6. The given size is 0"
                )
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }

            client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody("{ \"email\": \"email@test.com\"  }")
            }.let { response ->
                val body: ErrorDto = response.body()
                assertEquals(body.message, "Illegal input")
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }
        }

    @Test
    fun whenRegistrationIsSuccessful_whenTryingToRegisterAgainWithSameEmail_shouldRespondFailedState() =
        testWrapper {
            val client = getClient()

            val request = AuthRequest(getRandomEmail(), getRandomString(8))

            client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            // Try to create again. It should show username not available message.
            client.post(RouteConstants.accountRoute) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.let { response ->
                val body: Errors = response.body()
                assertEquals(body.errors[0].field, "email")
                assertEquals(body.errors[0].message, "Email already taken")
                assertEquals(HttpStatusCode.Forbidden, response.status)
            }
        }
}
