package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.User

class AuthController {

    fun login(ctx: Context) {
        val user = ctx.validatedBody<User>()
        ctx.json(user)
    }

    fun register(ctx: Context) {
        val user = ctx.validatedBody<User>()
        ctx.json(user)
    }
}