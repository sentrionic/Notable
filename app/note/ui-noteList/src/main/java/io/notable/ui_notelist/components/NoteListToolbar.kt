package io.notable.ui_notelist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.notable.ui_notelist.R
import io.notable.ui_notelist.ui.test.TAG_NOTE_FILTER_BTN
import io.notable.ui_notelist.ui.test.TAG_NOTE_SEARCH_BAR

@ExperimentalComposeUiApi
@Composable
fun NoteListToolbar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onExecuteSearch: () -> Unit,
    onShowFilterDialog: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .testTag(TAG_NOTE_SEARCH_BAR),
            value = query,
            onValueChange = {
                onQueryChanged(it)
                onExecuteSearch()
            },
            placeholder = { Text(text = "Search notes") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onExecuteSearch()
                    keyboardController?.hide()
                },
            ),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                IconButton(
                    modifier = Modifier.testTag(TAG_NOTE_FILTER_BTN),
                    onClick = {
                        onShowFilterDialog()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_filter_list_24),
                        contentDescription = "Filter Icon"
                    )
                }
            },
            textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
        )
    }
}