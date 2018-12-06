package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.UserDTO
import io.realworld.app.ext.isEmailValid

class AuthController {
    fun login(ctx: Context): UserDTO {
        val userRequest = ctx.validatedBody<UserDTO>()
                .check({ it.user.email?.isEmailValid() ?: false })
                .check({ !it.user.password.isNullOrBlank() })
                .getOrThrow()
        return UserDTO(userRequest.user)
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