package io.realworld.app.web.controllers

import io.javalin.http.Context
import io.realworld.app.domain.CommentDTO
import io.realworld.app.domain.CommentsDTO
import io.realworld.app.domain.service.CommentService

class CommentController(private val commentService: CommentService) {
    fun add(ctx: Context) {
        val slug = ctx.pathParam<String>("slug").get()
        ctx.bodyValidator<CommentDTO>()
                .check({ !it.comment?.body.isNullOrBlank() })
                .get().apply {
                    commentService.add(slug, ctx.attribute("email")!!, this.comment!!).also {
                        ctx.json(CommentDTO(it))
                    }
                }
    }

    fun findBySlug(ctx: Context) {
        ctx.pathParam<String>("slug").get().apply {
            commentService.findBySlug(this).also { comments ->
                ctx.json(CommentsDTO(comments))
            }
        }
    }

    fun delete(ctx: Context) {
        val slug = ctx.pathParam<String>("slug").get()
        val id = ctx.pathParam<Long>("id").get()
        commentService.delete(id, slug)
    }

}