package io.notable.ui_notelist.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.notable.note_datasource_test.network.data.NoteDataValid
import io.notable.note_datasource_test.network.serializeNoteData
import io.notable.shared.refresh.RefreshViewManager
import org.junit.Rule
import org.junit.Test

/**
 * Demo isolation test for NoteList screen.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class NoteListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val noteData = serializeNoteData(NoteDataValid.listData)

    @Test
    fun areNotesShown() {
        composeTestRule.setContent {
            val state = remember {
                NoteListState(
                    notes = noteData,
                    filteredNotes = noteData,
                )
            }
            NoteList(
                state = state,
                events = {},
                navigateToDetailScreen = {},
                navigateToFormScreen = {},
                refreshViewManager = RefreshViewManager(),
                handleLogout = {}
            )
        }

        for (i in 20 downTo 12) {
            composeTestRule.onNodeWithText("Untitled #$i").assertIsDisplayed()
        }
    }
}