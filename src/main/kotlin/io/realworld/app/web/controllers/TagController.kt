package io.realworld.app.web.controllers

import io.javalin.Context

class TagController {

    fun get(ctx: Context): Pair<String, List<String>> {
        return "tags" to listOf(" ")
    }

}