package io.realworld.app.web.controllers

import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO
import org.eclipse.jetty.http.HttpStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class UserControllerTest {

    companion object {
        val app = AppConfig.setup()
        val http = HttpUtil(app.port())
    }

    @Before
    fun init() {
        app.start()
    }

    @Test
    fun `invalid get current user without token`() {
        val response = http.get<UserDTO>("/api/user")

        assertEquals(response.status, HttpStatus.UNAUTHORIZED_401)
    }

    @Test
    fun `get current user by token`() {
        val response = http.get<UserDTO>("/api/user")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.user.username)
        assertNotNull(response.body.user.password)
        assertNotNull(response.body.user.token)
    }

    @Test
    fun `update user data`() {
        val userDTO = UserDTO(User(email = "update_user@update_test.com"))
        val response = http.put<UserDTO>("/api/user", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.user.email, userDTO.user.email)
    }
}