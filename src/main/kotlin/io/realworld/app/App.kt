package io.realworld.app

import io.realworld.app.config.AppConfig
import org.h2.tools.Server

fun main(args: Array<String>) {
    Server.createWebServer().start()
    AppConfig().setup().start()
}
