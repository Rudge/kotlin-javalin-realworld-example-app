package io.realworld.app.web.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.ProfileDTO
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO
import org.eclipse.jetty.http.HttpStatus
import org.h2.tools.Server
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class ProfileControllerTest {
    private lateinit var app: Javalin
    private lateinit var http: HttpUtil

    companion object {
        @BeforeClass
        @JvmStatic
        fun before() {
            Server.createTcpServer().start()
        }

        @AfterClass
        @JvmStatic
        fun after() {
            Server.createTcpServer().stop()
        }
    }

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
    fun `get profile by username`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val username = "celeb_username"
        val response = http.get<ProfileDTO>("/api/profiles/$username")

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile.username, username)
        assertNotNull(response.body.profile.bio)
        assertNotNull(response.body.profile.image)
    }

    @Test
    fun `follow profile by username`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val username = "celeb_username"
        val userDTO = UserDTO(User(email = "other_test@other_test.com", password = "Test"))
        val response = http.post<ProfileDTO>("/api/profiles/$username/follow", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile.username, username)
        assertNotNull(response.body.profile.bio)
        assertNotNull(response.body.profile.image)
        assertTrue(response.body.profile.following)
    }

    @Test
    fun `unfollow profile by username`() {
        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")

        val username = "celeb_username"
        val response = http.delete<ProfileDTO>("/api/profiles/$username/follow")

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile.username, username)
        assertNotNull(response.body.profile.bio)
        assertNotNull(response.body.profile.image)
        assertFalse(response.body.profile.following)
    }
}