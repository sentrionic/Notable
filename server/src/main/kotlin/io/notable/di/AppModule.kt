package io.notable.di

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig
import io.notable.dto.token.TokenConfig
import io.notable.services.*
import org.koin.dsl.module

private const val EXPIRATION_DATE = 365L * 1000L * 60L * 60L * 24L // 1 year

val appModule = module {
    single<TokenService> { JwtTokenService() }
    single<HashingService> { SHA256HashingService() }
    single { HoconApplicationConfig(ConfigFactory.load()) }
    single {
        val appConfig = get<HoconApplicationConfig>()
        TokenConfig(
            issuer = appConfig.property("jwt.issuer").getString(),
            audience = appConfig.property("jwt.audience").getString(),
            expiresIn = EXPIRATION_DATE,
            secret = appConfig.property("jwt.secret").getString()
        )
    }
    single<UserService> { UserServiceImpl(get(), get(), get()) }
    single<NoteService> { NoteServiceImpl() }
}
