package io.realworld.app

import io.realworld.app.config.AppConfig
import org.h2.tools.Server

fun main(args: Array<String>) {
    Server.createPgServer().start()
    AppConfig().setup().start()
}