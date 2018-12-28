package io.realworld.app.web.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.User
import io.realworld.app.domain.UserDTO
import io.realworld.app.web.ErrorResponse
import org.eclipse.jetty.http.HttpStatus
import org.h2.tools.Server
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class AuthControllerTest {
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
    fun `invalid login without pass valid body`() {
        val response = http.post<ErrorResponse>("/api/users/login",
                UserDTO())

        assertEquals(response.status, HttpStatus.UNPROCESSABLE_ENTITY_422)
        assertEquals(response.body["body"], "can't be empty")
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
        val userDTO = UserDTO(User(email = "success_register@valid_email.com", password = "Test", username =
        "username_test"))
        val response = http.post<UserDTO>("/api/users", userDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.user?.username, userDTO.user?.username)
        assertEquals(response.body.user?.password, userDTO.user?.password)
    }
}