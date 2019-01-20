package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.CommentDTO
import io.realworld.app.domain.CommentsDTO
import io.realworld.app.domain.service.CommentService

class CommentController(private val commentService: CommentService) {
    fun add(ctx: Context) {
        val slug = ctx.validatedPathParam("slug").getOrThrow()
        ctx.validatedBody<CommentDTO>()
                .check({ !it.comment?.body.isNullOrBlank() })
                .getOrThrow().apply {
                    commentService.add(slug, ctx.attribute("email")!!, this.comment!!).also {
                        ctx.json(CommentDTO(it))
                    }
                }
    }

    fun findBySlug(ctx: Context) {
        ctx.validatedPathParam("slug").getOrThrow().apply {
            commentService.findBySlug(this).also { comments ->
                ctx.json(CommentsDTO(comments))
            }
        }
    }

    fun delete(ctx: Context) {
        val slug = ctx.validatedPathParam("slug").getOrThrow()
        val id = ctx.validatedPathParam("id").asLong().getOrThrow()
        commentService.delete(id, slug)
    }

}