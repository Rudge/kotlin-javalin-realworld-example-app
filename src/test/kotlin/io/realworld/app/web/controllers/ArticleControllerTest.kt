package io.realworld.app.web.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.Article
import io.realworld.app.domain.ArticleDTO
import io.realworld.app.domain.ArticlesDTO
import org.eclipse.jetty.http.HttpStatus
import org.h2.tools.Server
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
        Server.createTcpServer().start()
        app = AppConfig().setup().start()
        http = HttpUtil(app.port())
    }

    @After
    fun cleanTokenHeader() {
        app.stop()
        Server.createTcpServer().stop()
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
        val email = "get_all_articles@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val response = http.get<ArticlesDTO>("/api/articles")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
    }

    @Test
    fun `get all articles by author`() {
        val author = "user_name_test"
        val response = http.get<ArticlesDTO>("/api/articles", mapOf("author" to author))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        response.body.articles.forEach { assertEquals(it.author?.username, author) }
    }

    @Test
    fun `get all articles by author with auth`() {
        val email = "get_all_articles_author@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)
        val author = "user_name_test"
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
        val email = "get_all_articles_favorited@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

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
        val email = "create_article@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val article = Article(title = "How to train your dragon",
                description = "Ever wonder how?",
                body = "Very carefully.",
                tagList = listOf("dragons", "training"))
        val response = http.post<ArticleDTO>("/api/articles", ArticleDTO(article))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertEquals(response.body.article?.title, article.title)
        assertEquals(response.body.article?.description, article.description)
        assertEquals(response.body.article?.body, article.body)
        //assertEquals(response.body.article?.tagList, article.tagList)
    }

    @Test
    fun `get all articles of feed`() {
        val email = "get_all_articles_feed@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val response = http.get<ArticlesDTO>("/api/articles/feed")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
    }

    @Test
    fun `get single article by slug`() {
        val slug = "how-to-train-your-dragon"
        val response = http.get<ArticleDTO>("/api/articles/$slug")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article?.body)
        assertNotNull(response.body.article?.title)
        assertNotNull(response.body.article?.description)
        assertNotNull(response.body.article?.tagList)
    }

    @Test
    fun `update article by slug`() {
        val email = "update_article@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val articleCreated = Article(title = "Update How to train your dragon",
                description = "Ever wonder how?",
                body = "Very carefully.",
                tagList = listOf("dragons", "training"))
        val responseCreated = http.post<ArticleDTO>("/api/articles", ArticleDTO(articleCreated))

        val slug = responseCreated.body.article?.slug
        val article = Article(body = "Very carefully.", title = "Teste", description = "Teste Desc")
        val response = http.put<ArticleDTO>("/api/articles/$slug", ArticleDTO(article))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertEquals(response.body.article?.body, article.body)
        assertNotNull(response.body.article?.title)
        assertNotNull(response.body.article?.description)
        assertNotNull(response.body.article?.tagList)
    }

    @Test
    fun `favorite article by slug`() {
        val email = "favorite_article@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val article = Article(title = "slug test",
                description = "Ever wonder how?",
                body = "Very carefully.",
                tagList = listOf("dragons", "training"))
        http.post<ArticleDTO>("/api/articles", ArticleDTO(article))

        val slug = "slug-test"
        val response = http.post<ArticleDTO>("/api/articles/$slug/favorite")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article?.body)
        assertNotNull(response.body.article?.title)
        assertNotNull(response.body.article?.description)
        assertNotNull(response.body.article?.tagList)
    }

    @Test
    fun `unfavorite article by slug`() {
        val email = "unfavorite_article@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val article = Article(title = "slug test 2",
                description = "Ever wonder how?",
                body = "Very carefully.",
                tagList = listOf("dragons", "training"))
        http.post<ArticleDTO>("/api/articles", ArticleDTO(article))

        val slug = "slug-test-2"
        val response = http.delete<ArticleDTO>("/api/articles/$slug/favorite")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article?.body)
        assertNotNull(response.body.article?.title)
        assertNotNull(response.body.article?.description)
        assertNotNull(response.body.article?.tagList)
    }

    @Test
    fun `delete article by slug`() {
        val email = "delete_article@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val slug = "slugTest"
        val response = http.deleteWithoutBody("/api/articles/$slug")

        assertEquals(response.status, HttpStatus.OK_200)
    }
}