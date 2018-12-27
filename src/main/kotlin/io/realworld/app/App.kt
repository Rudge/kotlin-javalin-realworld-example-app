package io.realworld.app

import io.realworld.app.config.AppConfig
import org.h2.tools.Server

fun main(args: Array<String>) {
    Server.createTcpServer().start()
    AppConfig().setup().start()
}