package io.realworld.app.web.controllers

import io.javalin.http.Context
import io.realworld.app.domain.CommentDTO
import io.realworld.app.domain.CommentsDTO
import io.realworld.app.domain.service.CommentService

class CommentController(private val commentService: CommentService) {
    fun add(ctx: Context) {
        val slug = ctx.pathParam("slug")
        ctx.bodyValidator<CommentDTO>()
            .check({ !it.comment?.body.isNullOrBlank() }, "Body is null")
            .get().apply {
                commentService.add(slug, ctx.attribute("email")!!, this.comment!!).also {
                    ctx.json(CommentDTO(it))
                }
            }
    }

    fun findBySlug(ctx: Context) {
        ctx.pathParam("slug").apply {
            commentService.findBySlug(this).also { comments ->
                ctx.json(CommentsDTO(comments))
            }
        }
    }

    fun delete(ctx: Context) {
        val slug = ctx.pathParam("slug")
        val id = ctx.pathParam("id")
        commentService.delete(id.toLong(), slug)
    }
}
