package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Comment
import io.realworld.app.domain.User
import java.util.*

class CommentController {

    //TODO TEMP
    private val user = User("", "", "", "", null, true)
    private val comment = Comment(0, Date(), Date(), "", user)

    fun add(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        val comment = ctx.validatedBody<Comment>()
        ctx.json(comment)
    }

    fun get(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        ctx.json(listOf(comment))
    }

    fun delete(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        val id = ctx.validatedPathParam("id").asLong()
    }

}