package io.notable.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.notable.core.domain.KQueue
import io.notable.core.domain.UIComponent
import kotlinx.coroutines.launch

@Composable
fun ProcessDialogQueue(
    queue: KQueue<UIComponent> = KQueue(mutableListOf()),
    onRemoveHeadFromQueue: () -> Unit,
    scaffoldState: ScaffoldState,
) {
    val scope = rememberCoroutineScope()

    if (!queue.isEmpty()) {
        queue.peek()?.let { uiComponent ->
            when (uiComponent) {
                is UIComponent.Dialog -> {
                    GenericDialog(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        title = uiComponent.title,
                        description = uiComponent.description,
                        onRemoveHeadFromQueue = onRemoveHeadFromQueue,
                    )
                }

                is UIComponent.SnackBar -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = uiComponent.message,
                            actionLabel = "Hide"
                        )
                    }
                }

                is UIComponent.AreYouSureDialog -> {
                    GenericDialog(
                        title = uiComponent.message,
                        description = uiComponent.description,
                        positiveAction = PositiveAction("Confirm") {
                            uiComponent.callback.proceed()
                            onRemoveHeadFromQueue()
                        },
                        negativeAction = NegativeAction("Cancel") {
                            uiComponent.callback.cancel()
                            onRemoveHeadFromQueue()
                        },
                        onRemoveHeadFromQueue = onRemoveHeadFromQueue,
                    )
                }

                is UIComponent.None -> {

                }
            }
        }
    }
}
