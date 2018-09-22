package io.realworld.app.web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.realworld.app.web.controllers.AuthController
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class Routes : KoinComponent {

    private val authController: AuthController by inject()

    fun register(app: Javalin) {
        app.routes {
            path("/api/users") {
                post("login", authController::login)
            }
        }
    }
}
