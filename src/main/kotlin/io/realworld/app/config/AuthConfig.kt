package io.realworld.app.config

import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.Context
import io.javalin.Javalin
import io.javalin.UnauthorizedResponse
import io.javalin.security.Role
import io.realworld.app.utils.JwtProvider

internal enum class Roles : Role {
    ANYONE, AUTHENTICATED

}

private const val headerTokenName = "Authorization"

class AuthConfig(private val jwtProvider: JwtProvider) {
    fun configure(app: Javalin) {
        app.accessManager { handler, ctx, permittedRoles ->
            val jwtToken = getJwtTokenHeader(ctx)
            val userRole = getUserRole(jwtToken) ?: Roles.ANYONE
            if (permittedRoles.contains(userRole)) {
                ctx.attribute("email", getUsername(jwtToken))
                handler.handle(ctx)
            } else {
                throw UnauthorizedResponse()
            }
        }
    }

    private fun getJwtTokenHeader(ctx: Context): DecodedJWT? {
        val tokenHeader = ctx.header(headerTokenName)?.substringAfter("Token")?.trim()

        if (tokenHeader != null) {
            return jwtProvider.decodeJWT(tokenHeader)
        }

        return null
    }

    private fun getUsername(jwtToken: DecodedJWT?): String? {
        return jwtToken?.subject
    }

    private fun getUserRole(jwtToken: DecodedJWT?): Role? {
        val userRole = jwtToken?.getClaim("role")?.asString() ?: return null
        return Roles.valueOf(userRole)
    }
}