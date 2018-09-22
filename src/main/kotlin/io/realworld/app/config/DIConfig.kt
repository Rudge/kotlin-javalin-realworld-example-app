package io.realworld.app.config

import io.realworld.app.web.controllers.*
import org.koin.dsl.module.module

class DIConfig {
    companion object {
        val myModule = module {
            single { AuthController() }
            single { UserController() }
            single { ProfileController() }
            single { ArticleController() }
            single { CommentController() }
            single { TagController() }
        }
    }
}