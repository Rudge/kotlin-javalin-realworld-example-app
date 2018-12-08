package io.realworld.app.config

import io.realworld.app.utils.JwtProvider
import io.realworld.app.web.Routes
import io.realworld.app.web.controllers.ArticleController
import io.realworld.app.web.controllers.AuthController
import io.realworld.app.web.controllers.CommentController
import io.realworld.app.web.controllers.ProfileController
import io.realworld.app.web.controllers.TagController
import io.realworld.app.web.controllers.UserController
import org.koin.dsl.module.module

object ModulesConfig {
    val myModule = module {
        single { AppConfig() }
        single { JwtProvider() }
        single { AuthConfig(get()) }
        single { Routes(get(), get(), get(), get(), get(), get()) }
        single { AuthController(get()) }
        single { UserController() }
        single { ProfileController() }
        single { ArticleController() }
        single { CommentController() }
        single { TagController() }
    }
}
