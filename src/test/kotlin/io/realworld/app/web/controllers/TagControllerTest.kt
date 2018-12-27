package io.realworld.app.web.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.domain.TagDTO
import org.eclipse.jetty.http.HttpStatus
import org.h2.tools.Server
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class TagControllerTest {
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
    fun `get all tags`() {
        val response = http.get<TagDTO>("/api/tags")

        assertEquals(response.status, HttpStatus.OK_200)
        assertTrue(response.body.tags.isNotEmpty())
    }
}