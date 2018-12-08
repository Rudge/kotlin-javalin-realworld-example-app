package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.config.Roles
import io.realworld.app.domain.UserDTO
import io.realworld.app.ext.isEmailValid
import io.realworld.app.utils.JwtProvider

class AuthController(private val jwtProvider: JwtProvider) {
    fun login(ctx: Context): UserDTO {
        val userRequest = ctx.validatedBody<UserDTO>()
                .check({ it.user.email?.isEmailValid() ?: false })
                .check({ !it.user.password.isNullOrBlank() })
                .getOrThrow()
        return UserDTO(userRequest.user.copy(token = jwtProvider.createJWT(userRequest.user, Roles.AUTHENTICATED)))
    }

    fun register(ctx: Context): UserDTO {
        val userRequest = ctx.validatedBody<UserDTO>()
                .check({ it.user.email?.isEmailValid() ?: false })
                .check({ !it.user.password.isNullOrBlank() })
                .check({ !it.user.username.isNullOrBlank() })
                .getOrThrow()
        return UserDTO(userRequest.user)
    }
}