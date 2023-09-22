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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserControllerTest {
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
    fun `invalid login without pass valid body`() {
        val response = http.post<ErrorResponse>(
            "/api/users/login",
            UserDTO(),
        )

        assertEquals(HttpStatus.BAD_REQUEST_400, response.status)
        val allErrors = response.body.allErrors
        assertTrue(allErrors.contains("Password is empty"))
    }

    @Test
    fun `success login with email and password`() {
        val email = "success_login@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "user_name_test")
        val userDTO = UserDTO(User(email = email, password = password))
        val response = http.post<UserDTO>("/api/users/login", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.user?.email, userDTO.user?.email)
        assertNotNull(response.body.user?.token)
    }

    @Test
    fun `success register user`() {
        val userDTO = UserDTO(
            User(
                email = "success_register@valid_email.com",
                password = "Test",
                username =
                "username_test",
            ),
        )
        val response = http.post<UserDTO>("/api/users", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.user?.username, userDTO.user?.username)
        assertEquals(response.body.user?.password, userDTO.user?.password)
    }

    @Test
    fun `invalid get current user without token`() {
        val response = http.get<ErrorResponse>("/api/user")

        assertEquals(response.status, HttpStatus.FORBIDDEN_403)
    }

    @Test
    fun `get current user by token`() {
        val email = "get_current@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test")
        http.loginAndSetTokenHeader(email, password)
        val response = http.get<UserDTO>("/api/user")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.user?.username)
        assertNotNull(response.body.user?.password)
        assertNotNull(response.body.user?.token)
    }

    @Test
    fun `update user data`() {
        val email = "email_valid@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test")

        http.loginAndSetTokenHeader("email_valid@valid_email.com", "Test")
        val userDTO = UserDTO(User(email = "update_user@update_test.com", password = "Test"))
        val response = http.put<UserDTO>("/api/user", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.user?.email, userDTO.user?.email)
    }
}
