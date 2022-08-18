apply {
    from("$rootDir/library-build.gradle")
}

plugins {
    kotlin(KotlinPlugins.serialization) version Kotlin.version
}


dependencies {
    "implementation"(project(Modules.authDataSource))
    "implementation"(project(Modules.constants))

    "implementation"(Ktor.ktorClientMock)
    "implementation"(Ktor.clientSerialization)
}