package io.notable.utils

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.notable.module

fun ApplicationTestBuilder.getClient() = createClient {
    install(ContentNegotiation) {
        json()
    }
}

fun testWrapper(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
    environment {
        config = MapApplicationConfig("jwt.realm" to "test")
    }

    application {
        module(koinModules = listOf(testAppModule))
    }

    block()
}
