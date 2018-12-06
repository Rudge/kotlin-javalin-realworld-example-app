package io.realworld.app.web.controllers

import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.ProfileDTO
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO
import org.eclipse.jetty.http.HttpStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileControllerTest {

    companion object {
        val app = AppConfig.setup()
        val http = HttpUtil(app.port())
    }

    @Before
    fun init() {
        app.start()
    }

    @Test
    fun `get profile by username`() {
        val username = "celeb_username"
        val response = http.get<ProfileDTO>("/api/profiles/$username")

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile.username, username)
        assertNotNull(response.body.profile.bio)
        assertNotNull(response.body.profile.image)
    }

    @Test
    fun `follow profile by username`() {
        val username = "celeb_username"
        val userDTO = UserDTO(User(email = "other_test@other_test.com"))
        val response = http.post<ProfileDTO>("/api/profiles/$username/follow", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile.username, username)
        assertNotNull(response.body.profile.bio)
        assertNotNull(response.body.profile.image)
        assertTrue(response.body.profile.following)
    }

    @Test
    fun `unfollow profile by username`() {
        val username = "celeb_username"
        val response = http.delete<ProfileDTO>("/api/profiles/$username/follow")

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.profile.username, username)
        assertNotNull(response.body.profile.bio)
        assertNotNull(response.body.profile.image)
        assertFalse(response.body.profile.following)
    }
}