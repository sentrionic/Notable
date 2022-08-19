package io.notable.routes.notes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.constants.RouteConstants
import io.notable.dto.note.NoteRequest
import io.notable.dto.note.NoteResponse
import io.notable.utils.*
import org.junit.After
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DeleteTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun whenDeleteNoteIsSuccessful_itShouldReturnTheDeletedNote() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            val request = NoteRequest(getRandomString(8), getRandomString(200))
            val note: NoteResponse = createNote(client, request, authToken)

            client.delete("${RouteConstants.noteRoute}/${note.id}") {
                contentType(ContentType.Application.Json)
                bearerAuth(authToken)
            }.let { response ->
                val deletedBody: NoteResponse = response.body()
                assertNotNull(deletedBody.id)
                assertNotNull(deletedBody.createdAt)
                assertNotNull(deletedBody.updatedAt)
                assertEquals(deletedBody.title, note.title)
                assertEquals(deletedBody.body, note.body)
                assertTrue(deletedBody.isDeleted)
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals(deletedBody.id, note.id)
            }
        }

    @Test
    fun whenAuthorizationIsNotProvided_shouldGetAuthorizationException() =
        testWrapper {
            val client = getClient()

            client.delete("${RouteConstants.noteRoute}/${getRandomId()}") {
                contentType(ContentType.Application.Json)
            }.let { response ->
                assertEquals(HttpStatusCode.Unauthorized, response.status)
            }
        }

    @Test
    fun whenTryingToDeleteANoteThatDoesNotBelongToTheUser_shouldGetAuthorizationException() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            val request = NoteRequest(getRandomString(8), getRandomString(200))
            val note: NoteResponse = createNote(client, request, authToken)

            client.delete("${RouteConstants.noteRoute}/${note.id}") {
                contentType(ContentType.Application.Json)
                bearerAuth(getAuthToken(client))
            }.let { response ->
                assertEquals(HttpStatusCode.Forbidden, response.status)
            }
        }

    @Test
    fun whenTryingToDeleteANoteThatDoesNotExist_itShouldReturnANoteNotFoundException() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            client.delete("${RouteConstants.noteRoute}/${getRandomId()}") {
                contentType(ContentType.Application.Json)
                bearerAuth(authToken)
            }.let { response ->
                assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }
}
