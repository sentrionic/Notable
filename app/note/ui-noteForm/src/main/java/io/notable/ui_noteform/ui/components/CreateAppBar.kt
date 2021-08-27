package io.notable.ui_noteform.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@ExperimentalComposeUiApi
@Composable
fun CreateAppBar(
    title: String,
    onSubmit: () -> Unit,
    navigateBack: () -> Unit,
    enabled: Boolean
) {
    val controller = LocalSoftwareKeyboardController.current

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(
                onClick = {
                    controller?.hide()
                    navigateBack()
                }
            ) {
                Icon(Icons.Filled.ArrowBack, "Navigate Back")
            }
        },
        actions = {
            IconButton(
                onClick = {
                    controller?.hide()
                    onSubmit()
                },
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Save Note Form"
                )
            }
        }
    )
}