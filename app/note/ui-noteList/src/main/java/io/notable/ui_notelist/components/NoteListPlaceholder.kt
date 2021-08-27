package io.notable.ui_notelist.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NoteListPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "No notes found",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h2,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Try another search term\nor add notes",
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
        )
    }
}