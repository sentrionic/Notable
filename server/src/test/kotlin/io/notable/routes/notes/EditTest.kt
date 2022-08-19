package io.notable.routes.notes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.constants.RouteConstants
import io.notable.dto.errors.ErrorDto
import io.notable.dto.errors.Errors
import io.notable.dto.note.NoteRequest
import io.notable.dto.note.NoteResponse
import io.notable.utils.*
import org.junit.After
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class EditTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun whenEditNoteIsSuccessful_itShouldReturnTheEditedNote() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            val createRequest = NoteRequest(getRandomString(8), getRandomString(200))
            val createBody: NoteResponse = createNote(client, createRequest, authToken)

            val updateRequest = NoteRequest(getRandomString(8), getRandomString(200))

            client.put("${RouteConstants.noteRoute}/${createBody.id}") {
                contentType(ContentType.Application.Json)
                setBody(updateRequest)
                bearerAuth(authToken)
            }.let { response ->
                val updateBody: NoteResponse = response.body()
                assertNotNull(updateBody.id)
                assertNotNull(updateBody.createdAt)
                assertNotNull(updateBody.updatedAt)
                assertEquals(updateBody.title, updateRequest.title)
                assertEquals(updateBody.body, updateRequest.body)
                assertFalse(updateBody.isDeleted)
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals(createBody.id, updateBody.id)
            }
        }

    @Test
    fun whenAuthorizationIsNotProvided_shouldGetAuthorizationException() =
        testWrapper {
            val client = getClient()

            val updateRequest = NoteRequest(getRandomString(8), getRandomString(200))

            client.put("${RouteConstants.noteRoute}/${getRandomId()}") {
                contentType(ContentType.Application.Json)
                setBody(updateRequest)
            }.let { response ->
                assertEquals(HttpStatusCode.Unauthorized, response.status)
            }
        }

    @Test
    fun whenTryingToEditANoteThatDoesNotBelongToTheUser_shouldGetAuthorizationException() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            val createRequest = NoteRequest(getRandomString(8), getRandomString(200))
            val createBody: NoteResponse = createNote(client, createRequest, authToken)

            val updateRequest = NoteRequest(getRandomString(8), getRandomString(200))

            client.put("${RouteConstants.noteRoute}/${createBody.id}") {
                contentType(ContentType.Application.Json)
                setBody(updateRequest)
                bearerAuth(getAuthToken(client))
            }.let { response ->
                assertEquals(HttpStatusCode.Forbidden, response.status)
            }
        }

    @Test
    fun whenTryingToEditANoteThatDoesNotExist_itShouldReturnANoteNotFoundException() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            val updateRequest = NoteRequest(getRandomString(8), getRandomString(200))

            client.put("${RouteConstants.noteRoute}/${getRandomId()}") {
                contentType(ContentType.Application.Json)
                setBody(updateRequest)
                bearerAuth(authToken)
            }.let { response ->
                assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }

    @Test
    fun whenEditNoteIsCalledWithAnInvalidTitle_itShouldReturnABadRequestException() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            client.put("${RouteConstants.noteRoute}/${getRandomId()}") {
                contentType(ContentType.Application.Json)
                setBody(NoteRequest("", getRandomString(200)))
                bearerAuth(authToken)
            }.let { response ->
                val body: Errors = response.body()
                assertEquals(body.errors[0].field, "title")
                assertEquals(body.errors[0].message, "\"title\" must not be empty")
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }

            client.put("${RouteConstants.noteRoute}/${getRandomId()}") {
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
