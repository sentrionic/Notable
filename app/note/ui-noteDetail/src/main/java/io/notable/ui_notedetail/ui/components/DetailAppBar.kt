package io.notable.ui_notedetail.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailAppBar(
    title: String,
    navigateBack: () -> Unit,
    handleEdit: () -> Unit,
    handleDelete: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
            )
        },
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.background,
        navigationIcon = {
            IconButton(
                onClick = { navigateBack() }
            ) {
                Icon(Icons.Filled.ArrowBack, "Navigate Back")
            }
        },
        actions = {
            NoteDropDownMenu(
                handleEdit = handleEdit,
                handleDelete = handleDelete
            )
        }
    )
}