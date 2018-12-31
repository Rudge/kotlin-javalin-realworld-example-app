package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.UserDTO
import io.realworld.app.domain.UserService
import io.realworld.app.ext.isEmailValid

class UserController(private val userService: UserService) {
    fun getCurrent(ctx: Context) {
        userService.getCurrent(ctx.attribute("email")).also { user ->
            ctx.json(UserDTO(user))
        }
    }

    fun update(ctx: Context) {
        val email = ctx.attribute<String>("email")
        ctx.validatedBody<UserDTO>()
                .check({ it.user != null })
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ it.user?.username?.isNotBlank() ?: true })
                .check({ it.user?.password?.isNotBlank() ?: true })
                .check({ it.user?.bio?.isNotBlank() ?: true })
                .check({ it.user?.image?.isNotBlank() ?: true })
                .getOrThrow()
                .user?.also { user ->
            userService.update(email, user).apply {
                ctx.json(UserDTO(this))
            }
        }

    }
}