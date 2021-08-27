package io.notable.ui_splash.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.notable.core.domain.KQueue
import io.notable.core.domain.ProgressBarState
import io.notable.shared.components.DefaultScreenUI
import io.notable.ui_splash.R

@Composable
fun SplashScreen(
) {
    DefaultScreenUI(
        queue = KQueue(mutableListOf()),
        onRemoveHeadFromQueue = {},
        progressBarState = ProgressBarState.Idle,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = "Icon",
                    )
                }

            }
        }
    }
}