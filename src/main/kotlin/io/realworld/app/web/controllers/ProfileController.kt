package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Response
import io.realworld.app.domain.User

class ProfileController {
    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)

    fun get(ctx: Context): Response {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return Response("user", user)
    }

    fun follow(ctx: Context): Response {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return Response("user", user)
    }

    fun unfollow(ctx: Context): Response {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return Response("user", user)
    }
}