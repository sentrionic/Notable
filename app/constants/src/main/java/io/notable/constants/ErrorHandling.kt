package io.notable.constants

object ErrorHandling {
    // Auth
    const val EMAIL_ALREADY_IN_USE = "Email already in use"
    const val INVALID_CREDENTIALS = "Invalid Credentials"
    const val INVALID_TOKEN = "Invalid token. Please logout and sign in again"
    const val UNKNOWN_ERROR = "Unknown error"
    const val SERVER_ERROR = "Server error. Try again later"
    const val NETWORK_DATA_ERROR = "Network Data Error"
    const val NOTE_NOT_FOUND = "No note with the given ID found"
    const val NOTE_DOES_NOT_EXIST = "That note does not exist in the cache."
    const val NO_INTERNET = "No network"
    const val NO_INTERNET_DESCRIPTION = "You need to be connected to the internet to delete the note"
}