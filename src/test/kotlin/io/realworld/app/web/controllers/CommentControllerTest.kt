package io.realworld.app.web.controllers

import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.Comment
import io.realworld.app.domain.CommentDTO
import io.realworld.app.domain.CommentsDTO
import org.eclipse.jetty.http.HttpStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommentControllerTest {

    companion object {
        val app = AppConfig.setup()
        val http = HttpUtil(app.port())
    }

    @Before
    fun init() {
        app.start()
    }

    @Test
    fun `add comment for article by slug`() {
        val slug = "slugTest"
        val comment = Comment(body = "Very carefully.")
        val response = http.post<CommentDTO>("/api/articles/$slug/comments",
                CommentDTO(comment))

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.comment.body, comment.body)
    }

    @Test
    fun `get all comments for article by slug`() {
        val slug = "slugTest"
        val response = http.get<CommentsDTO>("/api/articles/$slug/comments")

        assertEquals(response.status, HttpStatus.OK_200)
        assertTrue(response.body.comments.isNotEmpty())
    }

    @Test
    fun `delete comment for article by slug`() {
        val slug = "slugTest"
        val commentId = "1"
        val response = http.deleteWithoutBody("/api/articles/$slug/comments/$commentId")

        assertEquals(response.status, HttpStatus.OK_200)
    }
}