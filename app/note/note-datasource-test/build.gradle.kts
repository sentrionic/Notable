apply {
    from("$rootDir/library-build.gradle")
}

plugins {
    kotlin(KotlinPlugins.serialization) version Kotlin.version
}


dependencies {
    "implementation"(project(Modules.noteDataSource))
    "implementation"(project(Modules.noteDomain))
    "implementation"(project(Modules.constants))

    "implementation"(Ktor.ktorClientMock)
    "implementation"(Ktor.clientSerialization)
}