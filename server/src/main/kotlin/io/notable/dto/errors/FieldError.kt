package io.notable.dto.errors

import am.ik.yavi.core.ConstraintViolation
import am.ik.yavi.core.ConstraintViolations
import kotlinx.serialization.Serializable
import java.util.function.Consumer

@Serializable
data class Errors(val errors: List<FormError>) {
    @Serializable
    data class FormError(val field: String, val message: String)
}

fun formatErrors(violations: ConstraintViolations): Errors {
    val errors = mutableListOf<Errors.FormError>()
    violations.forEach(Consumer { x: ConstraintViolation -> errors.add(Errors.FormError(x.name(), x.message())) })
    return Errors(errors)
}

@Serializable
data class ErrorDto(val message: String, val errorCode: Int)
