package io.realworld.app.web.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO
import io.realworld.app.web.ErrorResponse
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AuthControllerTest {
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
    fun `invalid login without pass valid body`() {
        val response = http.post<ErrorResponse>("/api/users/login", UserDTO(User()))

        assertEquals(response.status, HttpStatus.UNPROCESSABLE_ENTITY_422)
        assertEquals(response.body["body"], "can't be empty")
    }

    @Test
    fun `success login with email and password`() {
        val userDTO = UserDTO(User(email = "email_valid@valid_email.com", password = "Test"))
        val response = http.post<UserDTO>("/api/users/login", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.user.email, userDTO.user.email)
        assertEquals(response.body.user.password, userDTO.user.password)
        assertNotNull(response.body.user.token)
    }

    @Test
    fun `success register user`() {
        val userDTO = UserDTO(User(email = "email_valid@valid_email.com", password = "Test", username = "username_test"))
        val response = http.post<UserDTO>("/api/users", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.user.username, userDTO.user.username)
        assertEquals(response.body.user.password, userDTO.user.password)
    }
}