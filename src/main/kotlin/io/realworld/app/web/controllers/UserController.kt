package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Response
import io.realworld.app.domain.User
import io.realworld.app.domain.UserRequest

class UserController {
    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)

    fun getCurrent(ctx: Context): Response {
        return Response("user", user)
    }

    fun update(ctx: Context): Response {
        val userRequest = ctx.validatedBody<UserRequest>().getOrThrow()
        return Response("user", userRequest.user)
    }
}