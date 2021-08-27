package io.notable.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

private const val duration = 300

@ExperimentalAnimationApi
fun slideExitTransition(width: Int): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -width },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(animationSpec = tween(duration))
}

@ExperimentalAnimationApi
fun slidePopEnterTransition(width: Int): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { -width },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(animationSpec = tween(duration))
}

@ExperimentalAnimationApi
fun slideEnterTransition(width: Int): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { width },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(animationSpec = tween(duration))
}

@ExperimentalAnimationApi
fun slidePopExitTransition(width: Int): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { width },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(animationSpec = tween(duration))
}