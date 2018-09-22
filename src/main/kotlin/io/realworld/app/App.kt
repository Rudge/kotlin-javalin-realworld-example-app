package io.realworld.app

import io.javalin.Javalin
import io.realworld.app.config.DIConfig
import io.realworld.app.web.Routes
import org.koin.standalone.StandAloneContext.startKoin

fun main(args: Array<String>) {
    val app = Javalin.create().enableCorsForAllOrigins().start(7000)
    startKoin(listOf(DIConfig.myModule))
    Routes.register(app)
}