package io.notable.ui_notelist.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.notable.core.domain.FilterOrder
import io.notable.note_domain.NoteFilter
import io.notable.ui_notelist.ui.test.TAG_NOTE_FILTER_DIALOG
import io.notable.ui_notelist.ui.test.TAG_NOTE_FILTER_DIALOG_DONE
import io.notable.ui_notelist.ui.test.TAG_NOTE_FILTER_LAST_UPDATE
import io.notable.ui_notelist.ui.test.TAG_NOTE_FILTER_NOTE_CHECKBOX

@ExperimentalAnimationApi
@Composable
fun NoteListFilter(
    noteFilter: NoteFilter,
    onUpdateNoteFilter: (NoteFilter) -> Unit,
    onCloseDialog: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier
            .padding(16.dp)
            .testTag(TAG_NOTE_FILTER_DIALOG),
        onDismissRequest = {
            onCloseDialog()
        },
        title = {
            Text(
                text = "Filter",
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            LazyColumn {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        // Title Filter
                        NoteFilterSelector(
                            filterOnNote = {
                                onUpdateNoteFilter(NoteFilter.Title())
                            },
                            isEnabled = noteFilter is NoteFilter.Title,
                            order = if (noteFilter is NoteFilter.Title) noteFilter.order else null,
                            orderDesc = {
                                onUpdateNoteFilter(
                                    NoteFilter.Title(
                                        order = FilterOrder.Descending
                                    )
                                )
                            },
                            orderAsc = {
                                onUpdateNoteFilter(
                                    NoteFilter.Title(
                                        order = FilterOrder.Ascending
                                    )
                                )
                            }
                        )
                        // Updated Filter
                        LastUpdatedFilterSelector(
                            filterOnLastUpdated = {
                                onUpdateNoteFilter(
                                    NoteFilter.UpdatedAt()
                                )
                            },
                            isEnabled = noteFilter is NoteFilter.UpdatedAt,
                            order = if (noteFilter is NoteFilter.UpdatedAt) noteFilter.order else null,
                            orderDesc = {
                                onUpdateNoteFilter(
                                    NoteFilter.UpdatedAt(
                                        order = FilterOrder.Descending
                                    )
                                )
                            },
                            orderAsc = {
                                onUpdateNoteFilter(
                                    NoteFilter.UpdatedAt(
                                        order = FilterOrder.Ascending
                                    )
                                )
                            },
                        )
                    }
                }
            }
        },
        buttons = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    // make the icon larger so it's easier to click
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag(TAG_NOTE_FILTER_DIALOG_DONE)
                        .clickable {
                            onCloseDialog()
                        },
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(10.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color(0xFF009a34)
                    )
                }

            }
        }
    )
}

/**
 * @param filterOnNote: Set the NoteFilter to 'Note'
 * @param isEnabled: Is the Note filter the selected 'NoteFilter'
 * @param order: Ascending or Descending?
 * @param orderDesc: Set the order to descending.
 * @param orderAsc: Set the order to ascending.
 */
@ExperimentalAnimationApi
@Composable
fun NoteFilterSelector(
    filterOnNote: () -> Unit,
    isEnabled: Boolean,
    order: FilterOrder? = null,
    orderDesc: () -> Unit,
    orderAsc: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag(TAG_NOTE_FILTER_NOTE_CHECKBOX)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null, // disable the highlight
                    enabled = true,
                    onClick = {
                        filterOnNote()
                    },
                ),
        ) {
            Checkbox(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically),
                checked = isEnabled,
                onCheckedChange = {
                    filterOnNote()
                },
                colors = CheckboxDefaults.colors(MaterialTheme.colors.primary)
            )
            Text(
                text = NoteFilter.Title().uiValue,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.SemiBold,
            )
        }

        OrderSelector(
            descString = "z -> a",
            ascString = "a -> z",
            isEnabled = isEnabled,
            isDescSelected = isEnabled && order is FilterOrder.Descending,
            isAscSelected = isEnabled && order is FilterOrder.Ascending,
            onUpdateNoteFilterDesc = {
                orderDesc()
            },
            onUpdateNoteFilterAsc = {
                orderAsc()
            },
        )
    }
}

/**
 * @param filterOnLastUpdated: Set the NoteFilter to 'ProWins'
 * @param isEnabled: Is the ProWins filter the selected 'NoteFilter'
 * @param order: Ascending or Descending?
 * @param orderDesc: Set the order to descending.
 * @param orderAsc: Set the order to ascending.
 */
@ExperimentalAnimationApi
@Composable
fun LastUpdatedFilterSelector(
    filterOnLastUpdated: () -> Unit,
    isEnabled: Boolean,
    order: FilterOrder? = null,
    orderDesc: () -> Unit,
    orderAsc: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .fillMaxWidth()
                .testTag(TAG_NOTE_FILTER_LAST_UPDATE)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    enabled = true,
                    onClick = {
                        filterOnLastUpdated()
                    },
                ),
        ) {
            Checkbox(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically),
                checked = isEnabled,
                onCheckedChange = {
                    filterOnLastUpdated()
                },
                colors = CheckboxDefaults.colors(MaterialTheme.colors.primary)
            )
            Text(
                text = NoteFilter.UpdatedAt().uiValue,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.SemiBold,
            )
        }

        OrderSelector(
            descString = "Newest",
            ascString = "Oldest",
            isEnabled = isEnabled,
            isDescSelected = isEnabled && order is FilterOrder.Descending,
            isAscSelected = isEnabled && order is FilterOrder.Ascending,
            onUpdateNoteFilterDesc = {
                orderDesc()
            },
            onUpdateNoteFilterAsc = {
                orderAsc()
            },
        )
    }
}