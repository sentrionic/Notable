apply {
    from("$rootDir/library-build.gradle")
}

plugins {
    kotlin(KotlinPlugins.serialization) version Kotlin.version
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.constants))
    "implementation"(project(Modules.noteDataSource))
    "implementation"(project(Modules.noteDomain))

    "implementation"(Kotlinx.coroutinesCore)

    "testImplementation"(project(Modules.noteDataSourceTest))
    "testImplementation"(Junit.junit4)
    "testImplementation"(Ktor.ktorClientMock)
    "testImplementation"(Ktor.clientSerialization)
}