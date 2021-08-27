apply {
    from("$rootDir/android-library-build.gradle")
}


dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.constants))
    "implementation"(project(Modules.noteDomain))

    "implementation"(AndroidX.datastore)

    "implementation"(Markwon.core)
    "implementation"(Markwon.strikethrough)
    "implementation"(Markwon.tables)
    "implementation"(Markwon.tasklist)
    "implementation"(Markwon.editor)
    "implementation"(Markwon.image)
}