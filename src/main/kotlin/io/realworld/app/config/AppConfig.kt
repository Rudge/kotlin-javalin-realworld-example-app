package io.realworld.app.config

import io.javalin.Context
import io.javalin.Javalin
import io.javalin.security.Role
import io.realworld.app.web.ErrorExceptionMapping
import io.realworld.app.web.Routes
import org.koin.core.KoinProperties
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.getProperty

object AppConfig : KoinComponent {
    fun setup(): Javalin {
        StandAloneContext.startKoin(listOf(ModulesConfig.myModule),
                KoinProperties(true, true))
        MapperConfig.configure()
        val app = Javalin.create()
                .enableCorsForAllOrigins()
                .contextPath(getProperty("context"))
//                    .accessManager { handler, ctx, permittedRoles ->
//                        val userRole = getUserRole(ctx) // determine user role based on request
//                        if (permittedRoles.contains(userRole)) {
//                            handler.handle(ctx)
//                        } else {
//                            ctx.status(401)
//                                    .result("Unauthorized")
//                        }
//                    }
        Routes.register(app)
        ErrorExceptionMapping.register(app)
        return app.port(getProperty("server_port"))
    }

    fun getUserRole(ctx: Context): Role {
        return MyRole.ANYONE
    }

    internal enum class MyRole : Role {
        ANYONE, ROLE_ONE, ROLE_TWO, ROLE_THREE
    }

}