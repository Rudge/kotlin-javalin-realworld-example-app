package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.User

class UserController {

    //TODO TEMP
    private val user = User("", "", "", "", null, true)

    fun getCurrent(ctx: Context) {
        ctx.json(user)
    }

    fun update(ctx: Context) {
        ctx.json(user)
    }
}