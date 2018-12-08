package io.realworld.app.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.security.Role
import io.realworld.app.domain.User

class JwtProvider {
    private val algorithm = Algorithm.HMAC256("something-very-secret-here")

    fun decodeJWT(token: String): DecodedJWT {
        return JWT.require(algorithm).build().verify(token)
    }

    fun createJWT(user: User, role: Role): String? {
        return JWT.create()
                .withClaim("username", user.username)
                .withClaim("role", role.toString())
                .sign(algorithm)
    }
}