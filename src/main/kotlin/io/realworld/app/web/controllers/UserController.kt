package io.realworld.app.web.controllers

import io.javalin.BadRequestResponse
import io.javalin.Context
import io.realworld.app.domain.User
import io.realworld.app.exception.InvalidRequestBodyException

class UserController {

    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)

    fun getCurrent(ctx: Context) {
        ctx.json(user)
    }

    fun update(ctx: Context) {
        try {
            val user = ctx.validatedBody<User>().getOrThrow()
            ctx.json(user)
        } catch (e: BadRequestResponse) {
            throw InvalidRequestBodyException()
        }
    }
}