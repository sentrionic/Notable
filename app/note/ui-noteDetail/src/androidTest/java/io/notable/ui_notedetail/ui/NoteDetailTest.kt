package io.notable.ui_notedetail.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.notable.note_datasource_test.network.data.NoteDataValid
import io.notable.note_datasource_test.network.serializeNoteData
import io.notable.shared.refresh.RefreshViewManager
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

/**
 * Demo isolation test for NoteDetail screen.
 */
@ExperimentalAnimationApi
class NoteDetailTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val noteData = serializeNoteData(NoteDataValid.listData)

    @Test
    fun isNoteDataShown() {
        // choose a note at random
        val note = noteData[Random.nextInt(0, noteData.size - 1)]
        composeTestRule.setContent {
            val state = remember {
                NoteDetailState(
                    note = note,
                )
            }
            NoteDetail(
                state = state,
                events = {},
                navigateBack = {},
                navigateToFormScreen = {},
                refreshViewManager = RefreshViewManager()
            )
        }

        composeTestRule.onNodeWithText(note.title).assertIsDisplayed()
    }

}