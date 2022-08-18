package io.notable.app.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String, val arguments: List<NamedNavArgument>) {

    object Splash : Screen(
        route = "splash",
        arguments = emptyList()
    )

    object AuthPage : Screen(
        route = "auth",
        arguments = emptyList()
    )

    object NoteList : Screen(
        route = "noteList",
        arguments = emptyList()
    )

    object NoteDetail : Screen(
        route = "noteDetail",
        arguments = listOf(navArgument("noteId") {
            type = NavType.StringType
        })
    )

    object NoteForm : Screen(
        route = "noteForm",
        arguments = listOf(navArgument("noteId") {
            type = NavType.StringType
        })
    )
}