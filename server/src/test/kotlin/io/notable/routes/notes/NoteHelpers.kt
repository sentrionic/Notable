package io.notable.routes.notes

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.constants.RouteConstants
import io.notable.dto.auth.AuthRequest
import io.notable.dto.auth.AuthResponse
import io.notable.dto.note.NoteRequest
import io.notable.dto.note.NoteResponse
import io.notable.utils.getRandomEmail
import io.notable.utils.getRandomString
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

suspend fun getAuthToken(client: HttpClient): String {
    val token = client.post(RouteConstants.accountRoute) {
        contentType(ContentType.Application.Json)
        setBody(AuthRequest(getRandomEmail(), getRandomString(8)))
    }.body<AuthResponse>().token

    assertNotNull(token)

    return token
}

suspend fun createNote(client: HttpClient, request: NoteRequest, authToken: String): NoteResponse {
    val response = client.post(RouteConstants.noteRoute) {
        contentType(ContentType.Application.Json)
        setBody(request)
        bearerAuth(authToken)
    }

    val body: NoteResponse = response.body()
    assertNotNull(body.id)
    assertNotNull(body.createdAt)
    assertNotNull(body.updatedAt)
    assertEquals(body.title, request.title)
    assertEquals(body.body, request.body)
    assertFalse(body.isDeleted)
    assertEquals(HttpStatusCode.Created, response.status)

    return body
}
