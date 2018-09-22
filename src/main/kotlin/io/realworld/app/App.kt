package io.realworld.app

import io.javalin.Javalin
import io.realworld.app.web.Routes
import io.realworld.app.web.controllers.AuthController
import io.realworld.app.web.controllers.UserController
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin

fun main(args: Array<String>) {
    val app = Javalin.create().enableCorsForAllOrigins().start(7000)
    startKoin(listOf(myModule))
    Routes().register(app)
}

val myModule = module {
    single { AuthController() }
    single { UserController() }
}