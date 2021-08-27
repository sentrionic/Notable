apply {
    from("$rootDir/library-build.gradle")
}

plugins {
    kotlin(KotlinPlugins.serialization) version Kotlin.version
    id(SqlDelight.plugin)
}

dependencies {
    "implementation"(project(Modules.noteDomain))
    "implementation"(project(Modules.constants))

    "implementation"(Ktor.core)
    "implementation"(Ktor.clientSerialization)
    "implementation"(Ktor.android)

    "implementation"(SqlDelight.runtime)
}

sqldelight {
    database("NoteDatabase") {
        packageName = "io.notable.note_datasource.cache"
        sourceFolders = listOf("sqldelight")
    }
}