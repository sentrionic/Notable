package io.notable.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.notable.dto.token.TokenClaim
import io.notable.dto.token.TokenConfig
import java.util.*

interface TokenService {
    fun generate(config: TokenConfig, vararg claims: TokenClaim): String
}

class JwtTokenService : TokenService {

    override fun generate(config: TokenConfig, vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
        claims.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret))
    }
}
