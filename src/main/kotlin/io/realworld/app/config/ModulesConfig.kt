package io.realworld.app.config

import io.realworld.app.domain.repository.ArticleRepository
import io.realworld.app.domain.repository.CommentRepository
import io.realworld.app.domain.repository.TagRepository
import io.realworld.app.domain.repository.UserRepository
import io.realworld.app.domain.service.ArticleService
import io.realworld.app.domain.service.CommentService
import io.realworld.app.domain.service.TagService
import io.realworld.app.domain.service.UserService
import io.realworld.app.utils.JwtProvider
import io.realworld.app.web.Router
import io.realworld.app.web.controllers.ArticleController
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
        single { Router(get(), get(), get(), get(), get()) }
    }
    private val userModule = module {
        single { UserController(get()) }
        single { UserService(get(), get()) }
        single { UserRepository(get()) }
    }
    private val articleModule = module {
        single { ArticleController(get()) }
        single { ArticleService(get(), get()) }
        single { ArticleRepository(get()) }
    }
    private val profileModule = module {
        single { ProfileController(get()) }
    }
    private val commentModule = module {
        single { CommentController(get()) }
        single { CommentService(get()) }
        single { CommentRepository(get()) }
    }
    private val tagModule = module {
        single { TagController(get()) }
        single { TagService(get()) }
        single { TagRepository(get()) }
    }
    internal val allModules = listOf(
        ModulesConfig.configModule,
        ModulesConfig.userModule,
        ModulesConfig.articleModule,
        ModulesConfig.profileModule,
        ModulesConfig.commentModule,
        ModulesConfig.tagModule,
    )
}
