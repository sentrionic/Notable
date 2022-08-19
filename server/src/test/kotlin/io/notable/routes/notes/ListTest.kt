package io.notable.routes.notes

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.constants.RouteConstants
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

class ListTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun whenListNotesIsSuccessful_itShouldReturnAListOfTheUsersNotes() =
        testWrapper {
            val client = getClient()
            val authToken = getAuthToken(client)

            val notes = mutableListOf<NoteResponse>()

            repeat(5) {
                val request = NoteRequest(getRandomString(8), getRandomString(200))
                val response = createNote(client, request, authToken)
                notes.add(response)
            }

            val response = client.get(RouteConstants.noteRoute) {
                contentType(ContentType.Application.Json)
                bearerAuth(authToken)
            }

            val body: List<NoteResponse> = response.body()
            assertEquals(5, body.size)
            assertEquals(body.map { it.id }, notes.map { it.id })
            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun whenAuthorizationIsNotProvided_shouldGetAuthorizationException() =
        testWrapper {
            val client = getClient()

            val response = client.get(RouteConstants.noteRoute) {
                contentType(ContentType.Application.Json)
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
}
