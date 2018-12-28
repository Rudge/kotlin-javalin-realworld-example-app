package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.UserDTO
import io.realworld.app.domain.UserService
import io.realworld.app.ext.isEmailValid

class AuthController(private val userService: UserService) {
    fun login(ctx: Context): UserDTO {
        val userRequest = ctx.validatedBody<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ !it.user?.password.isNullOrBlank() })
                .getOrThrow()
        return UserDTO(userService.authenticate(userRequest.user!!))
    }

    fun register(ctx: Context): UserDTO {
        val userRequest = ctx.validatedBody<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ !it.user?.password.isNullOrBlank() })
                .check({ !it.user?.username.isNullOrBlank() })
                .getOrThrow()
        return UserDTO(userService.create(userRequest.user!!))
    }
}