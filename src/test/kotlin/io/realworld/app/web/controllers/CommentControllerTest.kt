package io.realworld.app.web.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.Comment
import io.realworld.app.domain.CommentDTO
import io.realworld.app.domain.CommentsDTO
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommentControllerTest {
    private lateinit var app: Javalin
    private lateinit var http: HttpUtil

    @Before
    fun start() {
        app = AppConfig().setup().start()
        http = HttpUtil(app.port())
    }

    @After
    fun stop() {
        app.stop()
    }

    @Test
    fun `add comment for article by slug`() {
        val responseArticle = http.createArticle()

        val comment = Comment(body = "Very carefully.")
        val response = http.post<CommentDTO>(
            "/api/articles/${responseArticle.body.article?.slug}/comments",
            CommentDTO(comment),
        )

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.comment?.body, comment.body)
    }

    @Test
    fun `get all comments for article by slug`() {
        val responseArticle = http.createArticle()

        val slug = responseArticle.body.article?.slug

        val comment = Comment(body = "Very carefully.")
        http.post<CommentDTO>(
            "/api/articles/$slug/comments",
            CommentDTO(comment),
        )

        val response = http.get<CommentsDTO>("/api/articles/$slug/comments")

        assertEquals(response.status, HttpStatus.OK_200)
        assertTrue(response.body.comments.isNotEmpty())
        assertEquals(response.body.comments.first().body, comment.body)
    }

    @Test
    fun `delete comment for article by slug`() {
        val responseArticle = http.createArticle()

        val slug = responseArticle.body.article?.slug

        val comment = Comment(body = "Very carefully.")
        val responseAddComment = http.post<CommentDTO>(
            "/api/articles/$slug/comments",
            CommentDTO(comment),
        )

        val response = http.delete("/api/articles/$slug/comments/${responseAddComment.body.comment?.id}")

        assertEquals(response.status, HttpStatus.OK_200)
    }
}
