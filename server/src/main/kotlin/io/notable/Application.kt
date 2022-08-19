package io.notable

import io.ktor.server.application.*
import io.ktor.server.config.HoconApplicationConfig
import io.notable.db.DatabaseFactory
import io.notable.di.appModule
import io.notable.plugins.*
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.module.Module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@OptIn(ExperimentalSerializationApi::class)
@Suppress("unused")
fun Application.module(koinModules: List<Module> = listOf(appModule)) {
    install(Koin) {
        slf4jLogger()
        modules(koinModules)
    }

    val config: HoconApplicationConfig by inject()

    DatabaseFactory.init(config)

    configureSecurity()
    configureMonitoring()
    configureHTTP()
    configureSerialization()
    configureRouting()
    configureStatusPages()
}
