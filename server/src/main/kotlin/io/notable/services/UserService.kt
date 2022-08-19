package io.notable.services

import io.notable.constants.AppConstants
import io.notable.db.DatabaseFactory.dbQuery
import io.notable.db.User
import io.notable.db.UserTable
import io.notable.dto.auth.AuthRequest
import io.notable.dto.auth.AuthResponse
import io.notable.dto.auth.SaltedHash
import io.notable.dto.errors.BadCredentialsException
import io.notable.dto.token.TokenClaim
import io.notable.dto.token.TokenConfig
import java.util.UUID

interface UserService {
    suspend fun register(request: AuthRequest): AuthResponse
    suspend fun login(request: AuthRequest): AuthResponse
}

class UserServiceImpl(
    private val hashingService: HashingService,
    private val tokenService: TokenService,
    private val tokenConfig: TokenConfig
) : UserService {
    override suspend fun register(request: AuthRequest): AuthResponse {
        return dbQuery {
            val saltedHash = hashingService.generateSaltedHash(request.password)

            val user = User.new {
                email = request.email
                password = saltedHash.hash
                salt = saltedHash.salt
            }

            getAuthResponse(user.id.value)
        }
    }

    override suspend fun login(request: AuthRequest): AuthResponse {
        return dbQuery {
            val user = User.find { (UserTable.email eq request.email) }
                .firstOrNull() ?: throw BadCredentialsException()

            val isValidPassword = hashingService.verify(
                value = request.password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
            )

            if (!isValidPassword) {
                throw BadCredentialsException()
            }

            getAuthResponse(user.id.value)
        }
    }

    private fun getAuthResponse(userId: UUID): AuthResponse {
        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = AppConstants.userId,
                value = userId.toString()
            )
        )

        return AuthResponse(token)
    }
}
