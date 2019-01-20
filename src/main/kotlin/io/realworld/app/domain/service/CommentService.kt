package io.realworld.app.domain.service

import io.javalin.BadRequestResponse
import io.realworld.app.domain.Comment
import io.realworld.app.domain.repository.CommentRepository

class CommentService(private val commentRepository: CommentRepository) {
    fun add(slug: String, email: String?, comment: Comment): Comment? {
        if (email.isNullOrBlank()) throw BadRequestResponse()
        return commentRepository.add(slug, email, comment)
    }

    fun findBySlug(slug: String): List<Comment> {
        return commentRepository.findBySlug(slug)
    }

    fun delete(id: Long, slug: String) {
        commentRepository.delete(id, slug)
    }

}