package io.notable.validation

import am.ik.yavi.builder.validator
import io.notable.dto.note.NoteRequest

private const val MAX_TITLE_LENGTH = 50

val noteValidator = validator {
    NoteRequest::title {
        notNull()
        notEmpty()
        lessThanOrEqual(MAX_TITLE_LENGTH)
    }
}
