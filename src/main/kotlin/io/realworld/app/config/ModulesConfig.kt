package io.realworld.app.config

import io.realworld.app.domain.ArticleService
import io.realworld.app.domain.UserService
import io.realworld.app.domain.repository.ArticleRepository
import io.realworld.app.domain.repository.UserRepository
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
    private val configModule = module {
        single { AppConfig() }
        single { JwtProvider() }
        single { AuthConfig(get()) }
        single {
            DbConfig(getProperty("jdbc.url"), getProperty("db.username"), getProperty("db.password")).getDataSource()
        }
        single { Routes(get(), get(), get(), get(), get(), get()) }
    }
    private val userModule = module {
        single { AuthController(get()) }
        single { UserController() }
        single { UserService(get(), get()) }
        single { UserRepository(get()) }
    }
    private val articleModule = module {
        single { ArticleController(get()) }
        single { ArticleService(get(), get()) }
        single { ArticleRepository(get()) }
    }
    private val profileModule = module {
        single { ProfileController() }
    }
    private val commentModule = module {
        single { CommentController() }
    }
    private val tagModule = module {
        single { TagController() }
    }
    internal val allModules = listOf(ModulesConfig.configModule, ModulesConfig.userModule,
            ModulesConfig.articleModule, ModulesConfig.profileModule, ModulesConfig.commentModule,
            ModulesConfig.tagModule)
}
