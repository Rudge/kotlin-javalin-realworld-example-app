package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO

class ProfileController {
    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)

    fun get(ctx: Context): UserDTO {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return UserDTO(user)
    }

    fun follow(ctx: Context): UserDTO {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return UserDTO(user)
    }

    fun unfollow(ctx: Context): UserDTO {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return UserDTO(user)
    }
}