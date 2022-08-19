package io.notable.dto.errors

class AuthenticationException : RuntimeException()

class AuthorizationException : RuntimeException()

open class ValidationException(val params: Map<String, List<String>>) : RuntimeException()

class UserDoesNotExists : RuntimeException()

class NoteNotFoundException : RuntimeException()

class BadCredentialsException : RuntimeException()

class EmailAlreadyTakenException : RuntimeException()
