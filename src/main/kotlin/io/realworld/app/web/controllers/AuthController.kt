package io.realworld.app.web.controllers

import io.javalin.BadRequestResponse
import io.javalin.Context
import io.realworld.app.domain.User
import io.realworld.app.exception.InvalidRequestBodyException

class AuthController {

    fun login(ctx: Context) {
        try {
            val user = ctx.validatedBody<User>().getOrThrow()
            ctx.json(user)
        } catch (e: BadRequestResponse) {
            throw InvalidRequestBodyException()
        }
    }

    fun register(ctx: Context) {
        val user = ctx.validatedBody<User>().getOrThrow()
        ctx.json(user)
    }
}