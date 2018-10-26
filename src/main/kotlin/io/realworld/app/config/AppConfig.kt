package io.realworld.app.config

import io.javalin.Javalin
import io.realworld.app.web.ErrorExceptionMapping
import io.realworld.app.web.Routes

class AppConfig {

    companion object {
        fun configure(): Javalin {
            MapperConfig.configure()
            val app = Javalin.create()
                    .enableCorsForAllOrigins()
                    .contextPath("api")
            Routes.register(app)
            ErrorExceptionMapping.register(app)
            return app
        }
    }
}