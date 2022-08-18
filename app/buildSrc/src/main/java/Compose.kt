object Compose {
    private const val activityComposeVersion = "1.5.1"
    const val activity = "androidx.activity:activity-compose:$activityComposeVersion"

    const val composeVersion = "1.2.0"
    const val ui = "androidx.compose.ui:ui:$composeVersion"
    const val material = "androidx.compose.material:material:$composeVersion"
    const val tooling = "androidx.compose.ui:ui-tooling:$composeVersion"

    private const val navigationVersion = "2.5.1"
    const val navigation = "androidx.navigation:navigation-compose:$navigationVersion"

    private const val navigationHiltVersion = "1.0.0"
    const val navigationHilt = "androidx.hilt:hilt-navigation-compose:$navigationHiltVersion"
}

object ComposeTest {
    const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${Compose.composeVersion}"
    const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:${Compose.composeVersion}"
}