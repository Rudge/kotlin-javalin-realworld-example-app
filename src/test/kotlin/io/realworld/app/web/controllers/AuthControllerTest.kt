package io.realworld.app.web.controllers

import io.javalin.util.HttpUtil
import io.realworld.app.config.AppConfig
import io.realworld.app.config.DIConfig
import io.realworld.app.domain.User
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert
import org.koin.standalone.StandAloneContext

class AuthControllerTest : Spek({
    StandAloneContext.startKoin(listOf(DIConfig.myModule))
    val app = AppConfig.configure().start()
    val http = HttpUtil(app)

    describe("try to login - /api/users/login") {
        val login = http.post("/api/users/login")
        on("login without pass entity") {
            val response = login.asJson()
            it("should return http 422 and error info") {
                Assert.assertEquals(response.status, HttpStatus.UNPROCESSABLE_ENTITY_422)
                Assert.assertNotNull(response.body.`object`.get("body"))
            }
        }
        on("login with User") {
            val user = User(username = "Teste", password = "TEste")
            val response = login.body(user).asObject(User::class.java)
            it("should return http 200 and the user info") {
                Assert.assertEquals(response.status, HttpStatus.OK_200)
                Assert.assertEquals(response.body.username, user.username)
                Assert.assertEquals(response.body.password, user.password)
            }
        }
    }
})