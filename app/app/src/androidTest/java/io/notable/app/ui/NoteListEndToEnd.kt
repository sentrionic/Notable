package io.notable.app.ui

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.notable.app.MainActivity
import io.notable.app.di.NoteInteractorsModule
import io.notable.app.ui.navigation.Screen
import io.notable.app.ui.theme.NotableTheme
import io.notable.constants.INVALID_ID
import io.notable.note_datasource.cache.NoteCache
import io.notable.note_datasource.network.NoteService
import io.notable.note_datasource_test.cache.NoteCacheFake
import io.notable.note_datasource_test.cache.NoteDatabaseFake
import io.notable.note_datasource_test.network.NoteServiceListFake
import io.notable.note_datasource_test.network.NoteServiceResponseTypeList
import io.notable.note_interactors.*
import io.notable.shared.refresh.RefreshViewManager
import io.notable.ui_notedetail.ui.NoteDetail
import io.notable.ui_notedetail.ui.NoteDetailViewModel
import io.notable.ui_noteform.ui.NoteForm
import io.notable.ui_noteform.ui.NoteFormViewModel
import io.notable.ui_notelist.ui.NoteList
import io.notable.ui_notelist.ui.NoteListViewModel
import io.notable.ui_notelist.ui.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@UninstallModules(NoteInteractorsModule::class)
@HiltAndroidTest
class NoteListEndToEnd {

    @Module
    @InstallIn(SingletonComponent::class)
    object TestNoteInteractorsModule {

        @Provides
        @Singleton
        fun provideNoteCache(): NoteCache {
            return NoteCacheFake(NoteDatabaseFake())
        }

        @Provides
        @Singleton
        fun provideNoteService(): NoteService {
            return NoteServiceListFake.build(
                type = NoteServiceResponseTypeList.GoodData
            )
        }

        @Provides
        @Singleton
        fun provideNoteInteractors(
            cache: NoteCache,
            service: NoteService
        ): NoteInteractors {
            return NoteInteractors(
                syncNotes = SyncNotes(
                    cache = cache,
                    service = service,
                ),
                filterNotes = FilterNotes(),
                getNoteFromCache = GetNoteFromCache(
                    cache = cache,
                ),
                deleteNote = DeleteNote(
                    cache = cache,
                    service = service,
                ),
                createNote = CreateNote(
                    cache = cache,
                    service = service,
                ),
                clearDatabase = ClearDatabase(
                    cache = cache,
                ),
                updateNote = UpdateNote(
                    cache = cache,
                    service = service,
                ),
                syncDeletedNotes = SyncDeletedNotes(
                    cache = cache,
                    service = service,
                )
            )
        }

        @Provides
        @Singleton
        fun provideGetNoteFromCache(
            interactors: NoteInteractors,
        ): GetNoteFromCache {
            return interactors.getNoteFromCache
        }

        @Provides
        @Singleton
        fun provideDeleteNote(
            interactors: NoteInteractors,
        ): DeleteNote {
            return interactors.deleteNote
        }
    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun before() {
        composeTestRule.activity.setContent {
            NotableTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.NoteList.route,
                    builder = {
                        composable(
                            route = Screen.NoteList.route,
                        ) {
                            val viewModel: NoteListViewModel = hiltViewModel()
                            NoteList(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                navigateToDetailScreen = { noteId ->
                                    navController.navigate("${Screen.NoteDetail.route}/$noteId")
                                },
                                navigateToFormScreen = {
                                    navController.navigate("${Screen.NoteForm.route}/$INVALID_ID")
                                },
                                refreshViewManager = RefreshViewManager(),
                                handleLogout = {
                                    navController.navigate(Screen.AuthPage.route) {
                                        popUpTo(0)
                                    }
                                },
                            )
                        }

                        composable(
                            route = Screen.NoteDetail.route + "/{noteId}",
                            arguments = Screen.NoteDetail.arguments,
                        ) {
                            val viewModel: NoteDetailViewModel = hiltViewModel()
                            NoteDetail(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                navigateBack = {
                                    navController.popBackStack()
                                },
                                navigateToFormScreen = { noteId ->
                                    navController.navigate("${Screen.NoteDetail.route}/$noteId")
                                },
                                refreshViewManager = RefreshViewManager()
                            )
                        }

                        composable(
                            route = Screen.NoteForm.route + "/{noteId}",
                            arguments = Screen.NoteForm.arguments,
                        ) {
                            val viewModel: NoteFormViewModel = hiltViewModel()
                            NoteForm(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                navigateBack = {
                                    navController.popBackStack()
                                },
                            )
                        }
                    }
                )
            }
        }
    }

    @Test
    fun testSearchNoteByName() {
        composeTestRule.onRoot(useUnmergedTree = true)
            .printToLog("TAG") // For learning the ui tree system

        composeTestRule.onNodeWithTag(TAG_NOTE_SEARCH_BAR).performTextInput("Untitled #20")
        composeTestRule.onNodeWithTag(TAG_NOTE_TITLE, useUnmergedTree = true).assertTextEquals(
            "Untitled #20",
        )
        composeTestRule.onNodeWithTag(TAG_NOTE_SEARCH_BAR).performTextClearance()

        composeTestRule.onNodeWithTag(TAG_NOTE_SEARCH_BAR).performTextInput("Untitled #19")
        composeTestRule.onNodeWithTag(TAG_NOTE_TITLE, useUnmergedTree = true).assertTextEquals(
            "Untitled #19",
        )
        composeTestRule.onNodeWithTag(TAG_NOTE_SEARCH_BAR).performTextClearance()

        composeTestRule.onNodeWithTag(TAG_NOTE_SEARCH_BAR).performTextInput("Untitled #10")
        composeTestRule.onNodeWithTag(TAG_NOTE_TITLE, useUnmergedTree = true).assertTextEquals(
            "Untitled #10",
        )
    }

    @Test
    fun testFilterNoteAlphabetically() {
        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_BTN).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_DIALOG).assertIsDisplayed()

        // Filter by "Note" title
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_NOTE_CHECKBOX).performClick()

        // Order Descending (z-a)
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_DESC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTag(TAG_NOTE_TITLE, useUnmergedTree = true)
            .assertAny(hasText("Untitled #9"))

        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_BTN).performClick()

        // Order Ascending (a-z)
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_ASC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTag(TAG_NOTE_TITLE, useUnmergedTree = true)
            .assertAny(hasText("Untitled #1"))
    }

    @Test
    fun testFilterNoteByLastUpdated() {
        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_BTN).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_DIALOG).assertIsDisplayed()

        // Filter by ProWin %
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_LAST_UPDATE).performClick()

        // Order Descending (Newest)
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_DESC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTag(TAG_NOTE_TITLE, useUnmergedTree = true)
            .assertAny(hasText("Untitled #20"))

        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_BTN).performClick()

        // Order Ascending (Oldest)
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_ASC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_NOTE_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTag(TAG_NOTE_TITLE, useUnmergedTree = true)
            .assertAny(hasText("Untitled #1"))
    }
}