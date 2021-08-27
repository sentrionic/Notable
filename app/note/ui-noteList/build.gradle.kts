apply {
    from("$rootDir/android-library-build.gradle")
}


dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.shared))
    "implementation"(project(Modules.noteDomain))
    "implementation"(project(Modules.noteInteractors))

    "implementation"(Accompanist.swipe_refresh)
    "implementation"(SqlDelight.androidDriver)

    "androidTestImplementation"(project(Modules.noteDataSourceTest))
    "androidTestImplementation"(ComposeTest.uiTestJunit4)
    "debugImplementation"(ComposeTest.uiTestManifest)
    "androidTestImplementation"(Junit.junit4)
}