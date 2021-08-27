apply {
    from("$rootDir/android-library-build.gradle")
}


dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.constants))
    "implementation"(project(Modules.shared))
    "implementation"(project(Modules.noteDomain))
    "implementation"(project(Modules.noteInteractors))

    "implementation"(Accompanist.insets)
}