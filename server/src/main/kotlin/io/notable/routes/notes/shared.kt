package io.notable.routes.notes

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.notable.constants.RouteConstants
import io.notable.dto.errors.formatErrors
import io.notable.dto.note.NoteRequest
import io.notable.validation.noteValidator
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.IllegalArgumentException
import java.util.UUID

@Serializable
@Resource(RouteConstants.noteRoute)
class Notes(val search: String = "") {
    @Serializable
    @Resource(RouteConstants.idRoute)
    class Id(val parent: Notes = Notes(), @Serializable(with = UUIDSerializer::class) val id: UUID)

    @Serializable
    @Resource(RouteConstants.deletedRoute)
    class Deleted(val parent: Notes = Notes())
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return try {
            UUID.fromString(decoder.decodeString())
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Invalid UUID")
        }
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

suspend fun parseAndValidateNoteRequest(call: ApplicationCall): NoteRequest? {
    val request = call.receiveOrNull<NoteRequest>() ?: run {
        call.respond(HttpStatusCode.BadRequest)
        return null
    }

    val violations = noteValidator.validate(request)
    if (!violations.isValid) {
        call.respond(HttpStatusCode.BadRequest, formatErrors(violations))
        return null
    }

    return request
}
