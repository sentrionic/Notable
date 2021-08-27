package io.notable.ui_notelist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.notable.ui_notelist.R

@Composable
fun NoteListDrawer(
    handleLogout: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        item {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable {
                        handleLogout()
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_logout_black_24),
                    contentDescription = "Logout"
                )
                Box(
                    modifier = Modifier.width(10.dp)
                )
                Text("Logout")
            }
        }
    }
}