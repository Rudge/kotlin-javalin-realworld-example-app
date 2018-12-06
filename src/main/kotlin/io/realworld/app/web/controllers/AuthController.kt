package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.UserDTO

class AuthController {

    fun login(ctx: Context): UserDTO {
        val userRequest = ctx.body<UserDTO>()
        return UserDTO(userRequest.user)
    }

    fun register(ctx: Context): UserDTO {
        val userRequest = ctx.validatedBody<UserDTO>().getOrThrow()
        return UserDTO(userRequest.user)
    }
}