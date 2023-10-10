package io.realworld.app.web.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.ProfileDTO
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileControllerTest {
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
    fun `get profile by username`() {
        val email = "get_profile@valid_email.com"
        val password = "Test"
        http.registerUser("celeb_get_profile@valid_email.com", password, "celeb_username")
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val username = "celeb_username"
        val response = http.get<ProfileDTO>("/api/profiles/$username")

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile?.username, username)
        assertFalse(response.body.profile?.following ?: true)
    }

    @Test
    fun `follow profile by username`() {
        val email = "follow_profile@valid_email.com"
        val password = "Test"
        http.registerUser("celeb_follow_profile@valid_email.com", password, "celeb_username")
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val username = "celeb_username"
        val response = http.post<ProfileDTO>("/api/profiles/$username/follow")

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile?.username, username)
        assertTrue(response.body.profile?.following ?: false)
    }

    @Test
    fun `unfollow profile by username`() {
        val email = "unfollow_profile@valid_email.com"
        val password = "Test"
        http.registerUser("celeb_unfollow_profile@valid_email.com", password, "celeb_username")
        http.registerUser(email, password, "user_name_test")
        http.loginAndSetTokenHeader(email, password)

        val username = "celeb_username"
        val response = http.deleteWithResponseBody<ProfileDTO>("/api/profiles/$username/follow")

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile?.username, username)
        assertFalse(response.body.profile?.following ?: true)
    }
}
