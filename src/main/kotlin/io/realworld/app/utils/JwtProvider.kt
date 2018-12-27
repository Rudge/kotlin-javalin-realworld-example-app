package io.realworld.app.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.security.Role
import io.realworld.app.domain.User

class JwtProvider {

    fun decodeJWT(token: String): DecodedJWT {
        return JWT.require(Cipher.algorithm).build().verify(token)
    }

    fun createJWT(user: User, role: Role): String? {
        return JWT.create()
                .withClaim("email", user.email)
                .withClaim("role", role.toString())
                .sign(Cipher.algorithm)
    }
}