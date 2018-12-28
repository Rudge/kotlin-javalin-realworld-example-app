package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.UserDTO
import io.realworld.app.domain.UserService
import io.realworld.app.ext.isEmailValid

class UserController(private val userService: UserService) {
    fun getCurrent(ctx: Context) {
        ctx.json(UserDTO(userService.getCurrent(ctx.attribute("email"))))
    }

    fun update(ctx: Context) {
        val userRequest = ctx
                .validatedBody<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ it.user?.username?.isNotBlank() ?: true })
                .check({ it.user?.password?.isNotBlank() ?: true })
                .check({ it.user?.bio?.isNotBlank() ?: true })
                .check({ it.user?.image?.isNotBlank() ?: true })
                .getOrThrow()
        ctx.json(UserDTO(userService.update(ctx.attribute("email"), userRequest.user!!)))
    }
}