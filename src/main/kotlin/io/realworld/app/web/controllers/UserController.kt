package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.UserDTO
import io.realworld.app.domain.UserService
import io.realworld.app.ext.isEmailValid

class UserController(private val userService: UserService) {
    fun getCurrent(ctx: Context): UserDTO {
        return UserDTO(userService.getCurrent(ctx.attribute("email")))
    }

    fun update(ctx: Context): UserDTO {
        val userRequest = ctx
                .validatedBody<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ it.user?.username?.isNotBlank() ?: true })
                .check({ it.user?.password?.isNotBlank() ?: true })
                .check({ it.user?.bio?.isNotBlank() ?: true })
                .check({ it.user?.image?.isNotBlank() ?: true })
                .getOrThrow()
        return UserDTO(userService.update(userRequest.user!!))
    }
}