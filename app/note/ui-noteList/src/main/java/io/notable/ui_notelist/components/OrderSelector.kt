package io.notable.ui_notelist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.notable.ui_notelist.ui.test.TAG_NOTE_FILTER_ASC
import io.notable.ui_notelist.ui.test.TAG_NOTE_FILTER_DESC

/**
 * @param descString: String displayed in the "descending" checkbox
 * @param ascString: String displayed in the "ascending" checkbox
 * @param isEnabled: Is this NoteFilter currently the selected NoteFilter?
 * @param isDescSelected: Is the "descending" checkbox selected?
 * @param isAscSelected: Is the "ascending" checkbox selected?
 * @param onUpdateNoteFilterDesc: Set the filter to Descending.
 * @param onUpdateNoteFilterAsc: Set the filter to Ascending.
 */
@ExperimentalAnimationApi
@Composable
fun OrderSelector(
    descString: String,
    ascString: String,
    isEnabled: Boolean,
    isDescSelected: Boolean,
    isAscSelected: Boolean,
    onUpdateNoteFilterDesc: () -> Unit,
    onUpdateNoteFilterAsc: () -> Unit,
) {
    // Descending Order
    AnimatedVisibility(visible = isEnabled) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, bottom = 8.dp)
                .testTag(TAG_NOTE_FILTER_DESC)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null, // disable the highlight
                    enabled = isEnabled,
                    onClick = {
                        onUpdateNoteFilterDesc()
                    },
                ),
        ) {
            Checkbox(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically),
                enabled = isEnabled,
                checked = isEnabled && isDescSelected,
                onCheckedChange = {
                    onUpdateNoteFilterDesc()
                },
                colors = CheckboxDefaults.colors(MaterialTheme.colors.primary)
            )
            Text(
                text = descString,
                style = MaterialTheme.typography.body1,
            )
        }
    }

    // Ascending Order
    AnimatedVisibility(visible = isEnabled) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, bottom = 8.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null, // disable the highlight
                    enabled = isEnabled,
                    onClick = {
                        onUpdateNoteFilterAsc()
                    },
                ),
        ) {
            Checkbox(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag(TAG_NOTE_FILTER_ASC)
                    .align(Alignment.CenterVertically),
                enabled = isEnabled,
                checked = isEnabled && isAscSelected,
                onCheckedChange = {
                    onUpdateNoteFilterAsc()
                },
                colors = CheckboxDefaults.colors(MaterialTheme.colors.primary)
            )
            Text(
                text = ascString,
                style = MaterialTheme.typography.body1,
            )
        }
    }
}