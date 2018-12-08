package io.realworld.app.web.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.Article
import io.realworld.app.domain.ArticleDTO
import io.realworld.app.domain.ArticlesDTO
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ArticleControllerTest {
    private lateinit var app: Javalin
    private lateinit var http: HttpUtil

    @Before
    fun init() {
        app = AppConfig().setup().start()
        http = HttpUtil(app.port())
    }

    @After
    fun cleanTokenHeader() {
        app.stop()
    }

    @Test
    fun `get all articles`() {
        val response = http.get<ArticlesDTO>("/api/articles")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
    }

    @Test
    fun `get all articles with auth`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val response = http.get<ArticlesDTO>("/api/articles")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
    }

    @Test
    fun `get all articles by author`() {
        val author = "teste"
        val response = http.get<ArticlesDTO>("/api/articles", mapOf("author" to author))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        response.body.articles.forEach { assertEquals(it.author?.username, author) }
    }

    @Test
    fun `get all articles by author with auth`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")
        val author = "teste"
        val response = http.get<ArticlesDTO>("/api/articles", mapOf("author" to author))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        response.body.articles.forEach { assertEquals(it.author?.username, author) }
    }

    @Test
    fun `get all articles favorited by username`() {
        val response = http.get<ArticlesDTO>("/api/articles", mapOf("favorited" to "teste"))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
    }

    @Test
    fun `get all articles favorited by username with auth`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val response = http.get<ArticlesDTO>("/api/articles", mapOf("favorited" to "teste"))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
    }

    @Test
    fun `get all articles by tag`() {
        val tagName = "teste"
        val response = http.get<ArticlesDTO>("/api/articles", mapOf("tag" to tagName))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        response.body.articles.forEach { assertTrue(it.tagList.contains(tagName)) }
    }

    @Test
    fun `create article`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val article = Article(title = "How to train your dragon",
                description = "Ever wonder how?",
                body = "Very carefully.",
                tagList = listOf("dragons", "training"))
        val response = http.post<ArticleDTO>("/api/articles", ArticleDTO(article))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertEquals(response.body.article.title, article.title)
        assertEquals(response.body.article.description, article.description)
        assertEquals(response.body.article.body, article.body)
        assertEquals(response.body.article.tagList, article.tagList)
    }

    @Test
    fun `get all articles of feed`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val response = http.get<ArticlesDTO>("/api/articles/feed")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
    }

    @Test
    fun `get single article by slug`() {
        val slug = "slugTest"
        val response = http.get<ArticleDTO>("/api/articles/$slug")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article.body)
        assertNotNull(response.body.article.title)
        assertNotNull(response.body.article.description)
        assertNotNull(response.body.article.tagList)
    }

    @Test
    fun `update article by slug`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val slug = "slugTest"
        val article = Article(body = "Very carefully.")
        val response = http.put<ArticleDTO>("/api/articles/$slug", ArticleDTO(article))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertEquals(response.body.article.body, article.body)
        assertNotNull(response.body.article.title)
        assertNotNull(response.body.article.description)
        assertNotNull(response.body.article.tagList)
    }

    @Test
    fun `favorite article by slug`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val slug = "slugTest"
        val response = http.post<ArticleDTO>("/api/articles/$slug/favorite")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article.body)
        assertNotNull(response.body.article.title)
        assertNotNull(response.body.article.description)
        assertNotNull(response.body.article.tagList)
    }

    @Test
    fun `unfavorite article by slug`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val slug = "slugTest"
        val response = http.delete<ArticleDTO>("/api/articles/$slug/favorite")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article.body)
        assertNotNull(response.body.article.title)
        assertNotNull(response.body.article.description)
        assertNotNull(response.body.article.tagList)
    }

    @Test
    fun `delete article by slug`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val slug = "slugTest"
        val response = http.deleteWithoutBody("/api/articles/$slug")

        assertEquals(response.status, HttpStatus.OK_200)
    }
}