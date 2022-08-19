package io.notable.utils

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import io.notable.dto.token.TokenConfig
import io.notable.services.*
import org.koin.dsl.module

val testAppModule = module {
    single<TokenService> { JwtTokenService() }
    single<HashingService> { SHA256HashingService() }
    single { HoconApplicationConfig(ConfigFactory.load("application-test.conf")) }
    single {
        val appConfig = get<HoconApplicationConfig>()
        TokenConfig(
            issuer = appConfig.property("jwt.issuer").getString(),
            audience = appConfig.property("jwt.audience").getString(),
            expiresIn = 365L * 1000L * 60L * 60L * 24L,
            secret = appConfig.property("jwt.secret").getString()
        )
    }
    single<UserService> { UserServiceImpl(get(), get(), get()) }
    single<NoteService> { NoteServiceImpl() }
}
