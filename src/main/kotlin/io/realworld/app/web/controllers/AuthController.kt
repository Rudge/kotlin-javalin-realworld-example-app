package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.UserDTO
import io.realworld.app.domain.UserService
import io.realworld.app.ext.isEmailValid

class AuthController(private val userService: UserService) {
    fun login(ctx: Context) {
        ctx.validatedBody<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ !it.user?.password.isNullOrBlank() })
                .getOrThrow().user?.also { user ->
            userService.authenticate(user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }

    fun register(ctx: Context) {
        ctx.validatedBody<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ !it.user?.password.isNullOrBlank() })
                .check({ !it.user?.username.isNullOrBlank() })
                .getOrThrow().user?.also { user ->
            userService.create(user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }
}