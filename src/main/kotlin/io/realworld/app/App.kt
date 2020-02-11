package io.realworld.app

import io.realworld.app.config.AppConfig
import org.h2.tools.Server

fun main() {
    Server.createWebServer().start()
    AppConfig().setup().start()
}
