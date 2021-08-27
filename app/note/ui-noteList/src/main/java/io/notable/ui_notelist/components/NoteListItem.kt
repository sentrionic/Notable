package io.notable.ui_notelist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.notable.core.domain.DatetimeUtil
import io.notable.note_domain.Note
import io.notable.ui_notelist.ui.test.TAG_NOTE_DATE
import io.notable.ui_notelist.ui.test.TAG_NOTE_TITLE

@Composable
fun NoteListItem(
    note: Note,
    onSelectNote: (String) -> Unit,
) {
    val datetimeUtil = remember { DatetimeUtil() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .padding(horizontal = 15.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                onSelectNote(note.id)
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(60.dp)
                    .background(MaterialTheme.colors.primary),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(.8f) // fill 80% of remaining width
                    .padding(start = 12.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .testTag(TAG_NOTE_TITLE),
                    text = note.title,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .testTag(TAG_NOTE_DATE),
                    text = datetimeUtil.humanizeDatetime(note.createdAt),
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
            }
        }
    }
}