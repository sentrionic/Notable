package io.notable.ui_notedetail.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun NoteDropDownMenu(
    handleEdit: () -> Unit,
    handleDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { showMenu = true }
        ) {
            Icon(Icons.Filled.MoreVert, "Note Options")
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            DropdownMenuItem(
                onClick = {
                    showMenu = false
                    handleEdit()
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Note")
                    Text("Edit")
                }

            }

            Divider()

            DropdownMenuItem(
                onClick = {
                    showMenu = false
                    handleDelete()
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Note",
                        tint = MaterialTheme.colors.error
                    )
                    Text(text = "Delete")
                }

            }
        }
    }
}