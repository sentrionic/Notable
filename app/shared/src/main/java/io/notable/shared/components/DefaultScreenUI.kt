package io.notable.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.notable.core.domain.KQueue
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent

@Composable
fun DefaultScreenUI(
    queue: KQueue<UIComponent> = KQueue(mutableListOf()),
    onRemoveHeadFromQueue: () -> Unit,
    progressBarState: ProgressBarState = ProgressBarState.Idle,
    topBar: @Composable () -> Unit = {},
    drawer: @Composable (ColumnScope) -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    content: @Composable () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        drawerContent = drawer,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            content()

            ProcessDialogQueue(
                queue = queue,
                onRemoveHeadFromQueue = onRemoveHeadFromQueue,
                scaffoldState = scaffoldState
            )

            if (progressBarState is ProgressBarState.Loading) {
                CircularIndeterminateProgressBar()
            }

            DefaultSnackbar(
                snackbarHostState = scaffoldState.snackbarHostState,
                onDismiss = {
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}