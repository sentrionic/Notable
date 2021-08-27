package io.notable.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

/**
 * To be removed when [TextField]s support error
 */
@Composable
fun TextFieldError(
    textError: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = textError,
        modifier = modifier
            .fillMaxWidth(),
        style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error),
        fontSize = 13.sp,
    )
}