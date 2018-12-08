package io.realworld.app.config

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
            val userRole = getUserRole(ctx)
            if (permittedRoles.contains(userRole)) {
                handler.handle(ctx)
            } else {
                throw UnauthorizedResponse()
            }
        }
    }

    private fun getUserRole(ctx: Context): Role {
        val jwtToken = getTokenHeader(ctx)
        if (jwtToken.isNullOrBlank()) {
            return Roles.ANYONE
        }

        val userRole = jwtProvider.decodeJWT(jwtToken!!).getClaim("role").asString()

        return Roles.valueOf(userRole)
    }

    private fun getTokenHeader(ctx: Context): String? {
        return ctx.header(headerTokenName)?.substringAfter("Token")?.trim()
    }
}