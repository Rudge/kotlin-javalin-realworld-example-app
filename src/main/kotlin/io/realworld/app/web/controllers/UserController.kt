package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO
import io.realworld.app.ext.isEmailValid

class UserController {
    //TODO TEMP
    private val user = User(0, "", "", "", "", "", null)

    fun getCurrent(ctx: Context): UserDTO {
        return UserDTO(user)
    }

    fun update(ctx: Context): UserDTO {
        val userRequest = ctx
                .validatedBody<UserDTO>()
                .check({ it.user.email.isEmailValid() })
                .check({ it.user.username?.isNotBlank() ?: true })
                .check({ it.user.password.isNotBlank() })
                .check({ it.user.bio?.isNotBlank() ?: true })
                .check({ it.user.image?.isNotBlank() ?: true })
                .getOrThrow()
        return UserDTO(userRequest.user)
    }
}