package io.notable.validation

import am.ik.yavi.builder.validator
import io.notable.dto.auth.AuthRequest

private const val MIN_PASSWORD_LENGTH = 6
private const val MAX_PASSWORD_LENGTH = 150

val authValidator = validator {
    AuthRequest::email {
        notNull()
        notEmpty()
        email()
    }

    AuthRequest::password {
        notNull()
        greaterThanOrEqual(MIN_PASSWORD_LENGTH)
        lessThanOrEqual(MAX_PASSWORD_LENGTH)
    }
}
