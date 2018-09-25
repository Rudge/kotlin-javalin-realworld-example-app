package io.realworld.app

import io.realworld.app.config.AppConfig
import io.realworld.app.config.DIConfig
import org.koin.standalone.StandAloneContext


fun main(args: Array<String>) {
    StandAloneContext.startKoin(listOf(DIConfig.myModule))
    AppConfig.configure().start(7000)
}