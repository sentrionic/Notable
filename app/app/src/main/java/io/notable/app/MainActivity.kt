package io.notable.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import io.notable.app.ui.navigation.*
import io.notable.app.ui.theme.Grey1
import io.notable.app.ui.theme.NotableTheme
import io.notable.constants.INVALID_ID
import io.notable.shared.refresh.RefreshViewManager
import io.notable.shared.session.SessionEvents
import io.notable.shared.session.SessionManager
import io.notable.shared.util.ConnectivityManager
import io.notable.ui_auth.ui.AuthScreen
import io.notable.ui_auth.ui.AuthViewModel
import io.notable.ui_notedetail.ui.NoteDetail
import io.notable.ui_notedetail.ui.NoteDetailViewModel
import io.notable.ui_noteform.ui.NoteForm
import io.notable.ui_noteform.ui.NoteFormViewModel
import io.notable.ui_notelist.ui.NoteList
import io.notable.ui_notelist.ui.NoteListEvents
import io.notable.ui_notelist.ui.NoteListViewModel
import io.notable.ui_splash.ui.SplashScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var refreshManager: RefreshViewManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    override fun onStart() {
        super.onStart()
        connectivityManager.registerConnectionObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterConnectionObserver(this)
    }

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Notable)

        setContent {
            NotableTheme {
                ProvideWindowInsets {
                    val navController = rememberAnimatedNavController()

                    val systemUiController = rememberSystemUiController()
                    val useDarkIcons = MaterialTheme.colors.isLight

                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = Grey1,
                            darkIcons = useDarkIcons
                        )
                    }

                    BoxWithConstraints {
                        AnimatedNavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route,
                            builder = {
                                addSplashPage()
                                addAuthPage()
                                addNoteList(
                                    navController = navController,
                                    width = constraints.maxWidth / 2,
                                    refreshManager = refreshManager,
                                    logout = { sessionManager.onTriggerEvent(SessionEvents.Logout) },
                                )
                                addNoteDetail(
                                    navController = navController,
                                    width = constraints.maxWidth / 2,
                                    refreshManager = refreshManager,
                                )
                                addNoteForm(
                                    navController = navController,
                                    width = constraints.maxWidth / 2,
                                )
                            }
                        )

                        sessionManager.state.observe(this@MainActivity) { state ->
                            val route = when {
                                state.authToken != null -> Screen.NoteList.route
                                state.didCheckForPreviousAuthUser -> Screen.AuthPage.route
                                else -> Screen.Splash.route
                            }

                            navController.navigate(route) {
                                popUpTo(0)
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
fun NavGraphBuilder.addNoteList(
    navController: NavController,
    width: Int,
    refreshManager: RefreshViewManager,
    logout: () -> Unit,
) {
    composable(
        route = Screen.NoteList.route,
        exitTransition = {
            slideExitTransition(width)
        },
        popEnterTransition = {
            slidePopEnterTransition(width)
        },
    ) {
        val viewModel: NoteListViewModel = hiltViewModel()
        NoteList(
            state = viewModel.state.value,
            events = viewModel::onTriggerEvent,
            navigateToDetailScreen = { noteId ->
                navController.navigate("${Screen.NoteDetail.route}/$noteId")
            },
            navigateToFormScreen = {
                navController.navigate("${Screen.NoteForm.route}/${INVALID_ID}")
            },
            refreshViewManager = refreshManager,
            handleLogout = {
                viewModel.onTriggerEvent(NoteListEvents.OnClearDatabase)
                logout()
            },
        )
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addNoteDetail(
    navController: NavController,
    width: Int,
    refreshManager: RefreshViewManager
) {
    composable(
        route = Screen.NoteDetail.route + "/{noteId}",
        arguments = Screen.NoteDetail.arguments,
        enterTransition = {
            slideEnterTransition(width)
        },
        popExitTransition = {
            slidePopExitTransition(width)
        },
        exitTransition = {
            slideExitTransition(width)
        },
        popEnterTransition = {
            slidePopEnterTransition(width)
        },
    ) {
        val viewModel: NoteDetailViewModel = hiltViewModel()
        NoteDetail(
            state = viewModel.state.value,
            events = viewModel::onTriggerEvent,
            navigateBack = { navController.popBackStack() },
            navigateToFormScreen = { noteId ->
                navController.navigate("${Screen.NoteForm.route}/$noteId")
            },
            refreshViewManager = refreshManager
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
fun NavGraphBuilder.addNoteForm(
    navController: NavController,
    width: Int,
) {
    composable(
        route = Screen.NoteForm.route + "/{noteId}",
        arguments = Screen.NoteForm.arguments,
        enterTransition = {
            slideEnterTransition(width)
        },
        popExitTransition = {
            slidePopExitTransition(width)
        },
    ) {
        val viewModel: NoteFormViewModel = hiltViewModel()
        NoteForm(
            state = viewModel.state.value,
            events = viewModel::onTriggerEvent,
            navigateBack = { navController.popBackStack() },
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
fun NavGraphBuilder.addAuthPage() {
    composable(
        route = Screen.AuthPage.route,
    ) {
        val viewModel: AuthViewModel = hiltViewModel()
        AuthScreen(
            state = viewModel.state.value,
            events = viewModel::onTriggerEvent,
        )
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addSplashPage(
) {
    composable(
        route = Screen.Splash.route,
    ) {
        SplashScreen()
    }
}