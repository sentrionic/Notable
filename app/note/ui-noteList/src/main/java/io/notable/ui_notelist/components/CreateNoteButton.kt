package io.notable.ui_notelist.components

import androidx.compose.foundation.Image
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import io.notable.ui_notelist.R

@Composable
fun CreateNoteButton(
    navigateToFormScreen: () -> Unit,
) {
    FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        onClick = { navigateToFormScreen() },
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_compose),
            contentDescription = "Create post"
        )
    }
}