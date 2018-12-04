package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO

class UserController {
    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)

    fun getCurrent(ctx: Context): UserDTO {
        return UserDTO(user)
    }

    fun update(ctx: Context): UserDTO {
        val userRequest = ctx.validatedBody<UserDTO>().getOrThrow()
        return UserDTO(userRequest.user)
    }
}