package io.notable.routes.notes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.constants.RouteConstants
import io.notable.dto.errors.ErrorDto
import io.notable.dto.errors.Errors
import io.notable.dto.note.NoteRequest
import io.notable.utils.getClient
import io.notable.utils.getRandomString
import io.notable.utils.testWrapper
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreateTest : KoinTest {
    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun whenCreateNoteIsSuccessful_itShouldReturnTheCreatedNote() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            assertNotNull(authToken)

            val request = NoteRequest(getRandomString(8), getRandomString(200))
            createNote(client, request, authToken)
        }

    @Test
    fun whenAuthorizationIsNotProvided_shouldGetAuthorizationException() =
        testWrapper {
            val client = getClient()

            val request = NoteRequest(getRandomString(8), getRandomString(200))

            val response = client.post(RouteConstants.noteRoute) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun whenCreateNoteIsCalledWithAnInvalidTitle_itShouldReturnABadRequestException() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            assertNotNull(authToken)

            client.post(RouteConstants.noteRoute) {
                contentType(ContentType.Application.Json)
                setBody(NoteRequest("", getRandomString(200)))
                bearerAuth(authToken)
            }.let { response ->
                val body: Errors = response.body()
                assertEquals(body.errors[0].field, "title")
                assertEquals(body.errors[0].message, "\"title\" must not be empty")
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }

            client.post(RouteConstants.noteRoute) {
                contentType(ContentType.Application.Json)
                setBody("{ \"body\": \"This is a body\" }")
                bearerAuth(authToken)
            }.let { response ->
                val body: ErrorDto = response.body()
                assertEquals(body.message, "Illegal input")
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }
        }
}
