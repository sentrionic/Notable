package io.notable.ui_noteform.validation

import io.notable.shared.components.TextFieldState
import io.notable.ui_noteform.validation.TitleTextState.Companion.TEXT_MAX_LENGTH

class TitleTextState : TextFieldState(
    validator = ::isTextValid,
    errorFor = ::textValidationError,
) {
    companion object {
        const val TEXT_MAX_LENGTH = 30
    }
}

private fun isTextValid(text: String): Boolean {
    return text.isNotEmpty() && text.length <= TEXT_MAX_LENGTH
}

@Suppress("UNUSED_PARAMETER")
private fun textValidationError(text: String): String {
    return "Title must be between 1 and $TEXT_MAX_LENGTH characters"
}