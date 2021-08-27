package io.notable.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GenericDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    onRemoveHeadFromQueue: () -> Unit,
    positiveAction: PositiveAction? = null,
    negativeAction: NegativeAction? = null,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onRemoveHeadFromQueue()
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.body1,
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (negativeAction != null) {
                    TextButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            negativeAction.onNegativeAction()
                            onRemoveHeadFromQueue()
                        },
                    ) {
                        Text(
                            text = negativeAction.negativeBtnTxt,
                            color = Color.Black
                        )
                    }
                }

                if (positiveAction != null) {
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            positiveAction.onPositiveAction()
                            onRemoveHeadFromQueue()
                        },
                    ) {
                        Text(
                            text = positiveAction.positiveBtnTxt,
                            color = Color.White
                        )
                    }
                }
            }
        }
    )
}

data class PositiveAction(
    val positiveBtnTxt: String,
    val onPositiveAction: () -> Unit,
)

data class NegativeAction(
    val negativeBtnTxt: String,
    val onNegativeAction: () -> Unit,
)