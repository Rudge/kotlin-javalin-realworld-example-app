package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.User

class ProfileController {

    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)

    fun get(ctx: Context) {
        val username = ctx.validatedPathParam("username").getOrThrow()
        ctx.json(user)
    }

    fun follow(ctx: Context) {
        val username = ctx.validatedPathParam("username").getOrThrow()
        ctx.json(user)
    }

    fun unfollow(ctx: Context) {
        val username = ctx.validatedPathParam("username").getOrThrow()
        ctx.json(user)
    }
}