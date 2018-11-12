package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Response

class TagController {

    fun get(ctx: Context): Response {
        return Response("tags", listOf(" "))
    }

}