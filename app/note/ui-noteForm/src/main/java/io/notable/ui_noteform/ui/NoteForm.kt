package io.notable.ui_noteform.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import io.notable.core.domain.AreYouSureCallback
import io.notable.core.domain.UIComponent
import io.notable.shared.R
import io.notable.shared.components.DefaultScreenUI
import io.notable.shared.components.MarkdownEditor
import io.notable.shared.components.TextFieldError
import io.notable.ui_noteform.ui.components.CreateAppBar

@ExperimentalComposeUiApi
@Composable
fun NoteForm(
    state: NoteFormState,
    events: (NoteFormEvents) -> Unit,
    navigateBack: () -> Unit,
) {
    fun handleBackPress() {
        if (state.isDirty) {
            val callback: AreYouSureCallback = object : AreYouSureCallback {
                override fun proceed() {
                    navigateBack()
                }

                override fun cancel() {}
            }

            val message = UIComponent.AreYouSureDialog(
                message = "Confirmation",
                description = "Discard changes?",
                callback = callback,
            )
            events(NoteFormEvents.OnMessageReceived(message))
        } else {
            navigateBack()
        }
    }

    val padding = 20.dp

    DefaultScreenUI(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = {
            events(NoteFormEvents.OnRemoveHeadFromQueue)
        },
        progressBarState = state.progressBarState,
        topBar = {
            CreateAppBar(
                title = if (state.note != null) "Edit Note" else "Create Note",
                navigateBack = { handleBackPress() },
                onSubmit = { events(NoteFormEvents.OnSaveForm) },
                enabled = state.title.isValid
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                TextField(
                    placeholder = {
                        Text(
                            text = "Title",
                            style = MaterialTheme.typography.h4,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    maxLines = 1,
                    singleLine = true,
                    value = state.title.text,
                    onValueChange = { text ->
                        state.title.text = text
                        events(NoteFormEvents.OnUpdateTitle(text))
                    },
                    textStyle = MaterialTheme.typography.h4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = padding)
                        .onFocusChanged { focusState ->
                            state.title.onFocusChange(focusState.isFocused)
                            if (!focusState.isFocused) {
                                state.title.enableShowErrors()
                            }
                        },
                    isError = state.title.showErrors(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                    ),
                )
                state.title.getError()?.let { error ->
                    TextFieldError(
                        textError = error,
                        modifier = Modifier.padding(horizontal = padding)
                    )
                }
            }

            item {
                MarkdownEditor(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .imePadding(),
                    hintText = "Your note...",
                    fontResource = R.font.mulish_regular,
                    handleChange = { events(NoteFormEvents.OnUpdateBody(it)) },
                    value = state.body
                )
            }
        }
    }
}