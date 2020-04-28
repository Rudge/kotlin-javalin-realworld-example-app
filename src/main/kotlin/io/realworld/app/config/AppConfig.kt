package io.realworld.app.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.core.plugin.Plugin
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.plugin.json.JavalinJackson
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.realworld.app.config.ModulesConfig.allModules
import io.realworld.app.web.ErrorExceptionMapping
import io.realworld.app.web.Router
import io.swagger.v3.oas.models.info.Info
import org.eclipse.jetty.server.Server
import org.koin.core.KoinProperties
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.getProperty
import org.koin.standalone.inject
import java.text.SimpleDateFormat

class AppConfig : KoinComponent {
    private val authConfig: AuthConfig by inject()
    private val router: Router by inject()

    fun setup(): Javalin {
        StandAloneContext.startKoin(
                allModules,
                KoinProperties(true, true)
        )
        this.configureMapper()
        val app = Javalin.create { config ->
            config.apply {
                enableWebjars()
                enableCorsForAllOrigins()
                contextPath = getProperty("context")
                addStaticFiles("/swagger")
                addSinglePageRoot("","/swagger/swagger-ui.html")
                server {
                    Server(getProperty("server_port") as Int)
                }
            }
        }.events {
            it.serverStopping {
                StandAloneContext.stopKoin()
            }
        }
        authConfig.configure(app)
        router.register(app)
        ErrorExceptionMapping.register(app)
        return app
    }

    private fun configureMapper() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        JavalinJackson.configure(
                jacksonObjectMapper()
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .setDateFormat(dateFormat)
                        .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
        )
    }
}

