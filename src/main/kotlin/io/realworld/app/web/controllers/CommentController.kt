package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Comment
import io.realworld.app.domain.CommentDTO
import io.realworld.app.domain.CommentsDTO
import io.realworld.app.domain.User
import java.util.*

class CommentController {
    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)
    private val comment = Comment(0, Date(), Date(), "", user)

    fun add(ctx: Context): CommentDTO {
        val slug = ctx.validatedPathParam("slug")
        val commentValidate = ctx.validatedBody<Comment>()
        return CommentDTO(comment)
    }

    fun get(ctx: Context): CommentsDTO {
        val slug = ctx.validatedPathParam("slug")
        return CommentsDTO(listOf(comment))
    }

    fun delete(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        val id = ctx.validatedPathParam("id").asLong()
    }

}