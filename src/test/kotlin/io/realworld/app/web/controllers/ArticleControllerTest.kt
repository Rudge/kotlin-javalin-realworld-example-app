package io.realworld.app.web.controllers

import com.mashape.unirest.http.HttpResponse
import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.Article
import io.realworld.app.domain.ArticleDTO
import io.realworld.app.domain.ArticlesDTO
import io.realworld.app.domain.ProfileDTO
import io.realworld.app.domain.UserDTO
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ArticleControllerTest {
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

    private fun createUser(userEmail: String = "user@valid_user_mail.com", username: String = "user_name_test"): UserDTO {
        val password = "password"
        val user = http.registerUser(userEmail, password, username)
        http.loginAndSetTokenHeader(userEmail, password)
        return user
    }

    private fun createArticle(): HttpResponse<ArticleDTO> {
        return createArticle(Article(title = "How to train your dragon",
                description = "Ever wonder how?",
                body = "Very carefully.",
                tagList = listOf("dragons", "training")))
    }

    private fun createArticle(article: Article): HttpResponse<ArticleDTO> {
        createUser()
        return http.post<ArticleDTO>("/api/articles", ArticleDTO(article))
    }

    @Test
    fun `get all articles`() {
        createArticle()
        val http = HttpUtil(app.port())
        val response = http.get<ArticlesDTO>("/api/articles")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
    }

    @Test
    fun `get all articles with auth`() {
        createArticle()
        val response = http.get<ArticlesDTO>("/api/articles")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        assertNotNull(response.body.articles.first())
        assertFalse(response.body.articles.first().title.isNullOrBlank())
        assertTrue(response.body.articles.first().tagList.isNotEmpty())
    }

    @Test
    fun `get all articles by author`() {
        val author = "user_name_test"
        val response = http.get<ArticlesDTO>("/api/articles?author=$author")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        response.body.articles.forEach {
            assertEquals(it.author?.username, author)
            assertFalse(it.title.isNullOrBlank())
            assertTrue(it.tagList.isNotEmpty())
        }
    }

    @Test
    fun `get all articles by author with auth`() {
        createArticle()
        val author = "user_name_test"
        val response = http.get<ArticlesDTO>("/api/articles?author=$author")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        response.body.articles.forEach {
            assertEquals(it.author?.username, author)
            assertFalse(it.title.isNullOrBlank())
            assertTrue(it.tagList.isNotEmpty())
        }
    }

    @Test
    fun `get all articles favorited by username`() {
        val responseCreate = createArticle()
        http.post<ArticleDTO>("/api/articles/${responseCreate.body.article?.slug}/favorite")

        val response = http.get<ArticlesDTO>("/api/articles?favorited=user_name_test")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        assertNotNull(response.body.articles.first())
        assertFalse(response.body.articles.first().title.isNullOrBlank())
        assertTrue(response.body.articles.first().tagList.isNotEmpty())
        assertTrue(response.body.articles.first().favorited)
        assertTrue(response.body.articles.first().favoritesCount > 0)
    }

    @Test
    fun `get all articles favorited by username with auth`() {
        val responseCreate = createArticle()
        http.post<ArticleDTO>("/api/articles/${responseCreate.body.article?.slug}/favorite")

        val response = http.get<ArticlesDTO>("/api/articles?favorited=${responseCreate.body.article?.author?.username}")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        assertNotNull(response.body.articles.first())
        assertFalse(response.body.articles.first().title.isNullOrBlank())
        assertTrue(response.body.articles.first().tagList.isNotEmpty())
    }

    @Test
    fun `get all articles by tag`() {
        val responseCreate = createArticle()
        val tag = responseCreate.body.article?.tagList?.first()
        val response = http.get<ArticlesDTO>("/api/articles?tag=${responseCreate.body.article?.tagList?.first()}")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        assertTrue(response.body.articles.first().tagList.contains(tag))
    }

    @Test
    fun `create article`() {
        val article = Article(title = "Create How to train your dragon",
                description = "Ever wonder how?",
                body = "Very carefully.",
                tagList = listOf("create_article"))
        val response = createArticle(article)
        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertEquals(response.body.article?.title, article.title)
        assertEquals(response.body.article?.description, article.description)
        assertEquals(response.body.article?.body, article.body)
        assertEquals(response.body.article?.tagList, article.tagList)
    }

    @Test
    fun `get all articles of feed`() {
        createArticle()

        val http = HttpUtil(app.port())
        val email = "celeb_follow_profile@valid_email.com"
        val password = "password"
        http.registerUser(email, password, "celeb_username")
        http.loginAndSetTokenHeader(email, password)
        http.post<ProfileDTO>("/api/profiles/user_name_test/follow")

        val response = http.get<ArticlesDTO>("/api/articles/feed")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.articles)
        assertEquals(response.body.articles.size, response.body.articlesCount)
        assertNotNull(response.body.articles.first())
        assertFalse(response.body.articles.first().title.isNullOrBlank())
        assertTrue(response.body.articles.first().tagList.isNotEmpty())
    }

    @Test
    fun `get single article by slug`() {
        val responseArticle = createArticle()
        val slug = responseArticle.body.article?.slug
        val response = http.get<ArticleDTO>("/api/articles/$slug")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article?.body)
        assertFalse(response.body.article?.title.isNullOrBlank())
        assertNotNull(response.body.article?.description)
        assertTrue(response.body.article?.tagList?.isNotEmpty() ?: false)
    }

    @Test
    fun `update article by slug`() {
        val responseCreated = createArticle()
        val slug = responseCreated.body.article?.slug
        val article = Article(body = "Very carefully.", title = "Teste", description = "Teste Desc")
        val response = http.put<ArticleDTO>("/api/articles/$slug", ArticleDTO(article))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertEquals(response.body.article?.body, article.body)
        assertNotNull(response.body.article?.body)
        assertFalse(response.body.article?.title.isNullOrBlank())
        assertNotNull(response.body.article?.description)
        assertTrue(response.body.article?.tagList?.isNotEmpty() ?: false)
    }

    @Test
    fun `favorite article by slug`() {
        val article = Article(title = "slug test",
                description = "Ever wonder how?",
                body = "Very carefully.",
                tagList = listOf("favorite"))
        createArticle(article)
        val slug = "slug-test"
        val response = http.post<ArticleDTO>("/api/articles/$slug/favorite")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article?.body)
        assertFalse(response.body.article?.title.isNullOrBlank())
        assertNotNull(response.body.article?.description)
        assertTrue(response.body.article?.tagList?.isNotEmpty() ?: false)
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
                tagList = listOf("unfavorite"))
        http.post<ArticleDTO>("/api/articles", ArticleDTO(article))
        val slug = "slug-test-2"
        val response = http.delete<ArticleDTO>("/api/articles/$slug/favorite")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.article)
        assertNotNull(response.body.article?.body)
        assertFalse(response.body.article?.title.isNullOrBlank())
        assertNotNull(response.body.article?.description)
        assertTrue(response.body.article?.tagList?.isNotEmpty() ?: false)
    }

    @Test
    fun `delete article by slug`() {
        val responseCreate = createArticle()
        val response = http.deleteWithoutBody("/api/articles/${responseCreate.body.article?.slug}")

        assertEquals(response.status, HttpStatus.OK_200)
    }
}