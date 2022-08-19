package io.notable.routes.notes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.constants.RouteConstants
import io.notable.dto.note.DeleteNotesRequest
import io.notable.dto.note.NoteRequest
import io.notable.dto.note.NoteResponse
import io.notable.utils.getClient
import io.notable.utils.getRandomString
import io.notable.utils.testWrapper
import org.junit.After
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteManyTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun whenDeleteManyNotesIsSuccessful_itShouldReturnAStatusOK() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            val notes = mutableListOf<NoteResponse>()

            repeat(5) {
                val request = NoteRequest(getRandomString(8), getRandomString(200))
                val response = createNote(client, request, authToken)
                notes.add(response)
            }

            client.delete(RouteConstants.noteRoute) {
                contentType(ContentType.Application.Json)
                setBody(DeleteNotesRequest(notes.map { it.id }))
                bearerAuth(authToken)
            }.let { response ->
                assertEquals(HttpStatusCode.OK, response.status)
            }

            client.get(RouteConstants.noteRoute + RouteConstants.deletedRoute) {
                contentType(ContentType.Application.Json)
                bearerAuth(authToken)
            }.let { response ->
                val body: List<NoteResponse> = response.body()
                assertEquals(5, body.size)
                assertEquals(body.map { it.id }, notes.map { it.id })
                body.forEach { note ->
                    assertTrue(note.isDeleted)
                }
                assertEquals(HttpStatusCode.OK, response.status)
            }
        }

    @Test
    fun whenAuthorizationIsNotProvided_shouldGetAuthorizationException() =
        testWrapper {
            val client = getClient()

            client.delete(RouteConstants.noteRoute) {
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

            client.delete(RouteConstants.noteRoute) {
                contentType(ContentType.Application.Json)
                bearerAuth(getAuthToken(client))
                setBody(DeleteNotesRequest(listOf(note.id)))
            }.let { response ->
                assertEquals(HttpStatusCode.Forbidden, response.status)
            }
        }

    @Test
    fun whenUsingAnInvalidUUID_itShouldReturnABadRequestException() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            client.delete(RouteConstants.noteRoute) {
                contentType(ContentType.Application.Json)
                bearerAuth(authToken)
                setBody(DeleteNotesRequest(listOf(getRandomString(5))))
            }.let { response ->
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }
        }
}
