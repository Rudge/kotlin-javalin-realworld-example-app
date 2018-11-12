package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Response
import io.realworld.app.domain.UserRequest

class AuthController {

    fun login(ctx: Context): Response {
        val userRequest = ctx.validatedBody<UserRequest>().getOrThrow()
        return Response("user", userRequest.user)
    }

    fun register(ctx: Context): Response {
        val userRequest = ctx.validatedBody<UserRequest>().getOrThrow()
        return Response("user", userRequest.user)
    }
}