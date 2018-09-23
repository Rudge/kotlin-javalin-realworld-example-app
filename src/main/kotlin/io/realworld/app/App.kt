package io.realworld.app

import io.javalin.Javalin
import io.realworld.app.config.DIConfig
import io.realworld.app.config.MapperConfig
import io.realworld.app.web.Routes
import org.koin.standalone.StandAloneContext.startKoin

private val JSON_COTENT_TYPE = "application/json; charset=utf-8"

fun main(args: Array<String>) {
    val app = Javalin.create().enableCorsForAllOrigins()
            .defaultContentType(JSON_COTENT_TYPE).start(7000)
    MapperConfig.configure()
    startKoin(listOf(DIConfig.myModule))
    Routes.register(app)
}