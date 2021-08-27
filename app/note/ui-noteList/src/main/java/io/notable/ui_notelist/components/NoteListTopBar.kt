package io.notable.ui_notelist.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun NoteListTopBar(
    scaffoldState: ScaffoldState,
) {
    val scope = rememberCoroutineScope()

    TopAppBar(
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        title = {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
    )
}