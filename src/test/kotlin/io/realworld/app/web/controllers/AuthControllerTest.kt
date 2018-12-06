package io.realworld.app.web.controllers

import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO
import io.realworld.app.web.ErrorResponse
import org.eclipse.jetty.http.HttpStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class AuthControllerTest {

    companion object {
        val app = AppConfig.setup()
        val http = HttpUtil(app)
        @BeforeClass
        fun setup() {
            val app = AppConfig.setup()
            val http = HttpUtil(app)
        }
    }

    @Before
    fun init() {
        app.start()
    }

    @Test
    @Ignore
    fun `invalid authentication without pass body`() {
        val response = http.post<ErrorResponse>("/api/users/login", UserDTO(User()))

        assertEquals(response.status, HttpStatus.UNPROCESSABLE_ENTITY_422)
        assertEquals(response.body["body"], "can't be empty")
    }

    @Test
    fun `success authentication with email and password`() {
        val userDTO = UserDTO(User(email = "Test", password = "Test"))
        val response = http.post<UserDTO>("/api/users/login", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.user.username, userDTO.user.username)
        assertEquals(response.body.user.password, userDTO.user.password)
    }
}