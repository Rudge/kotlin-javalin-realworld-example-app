package io.realworld.app.web

import io.javalin.Context
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.javalin.security.SecurityUtil.roles
import io.realworld.app.config.Roles
import io.realworld.app.web.controllers.ArticleController
import io.realworld.app.web.controllers.AuthController
import io.realworld.app.web.controllers.CommentController
import io.realworld.app.web.controllers.ProfileController
import io.realworld.app.web.controllers.TagController
import io.realworld.app.web.controllers.UserController
import org.koin.standalone.KoinComponent
import kotlin.reflect.KFunction1

class Routes(private val authController: AuthController,
             private val userController: UserController,
             private val profileController: ProfileController,
             private val articleController: ArticleController,
             private val commentController: CommentController,
             private val tagController: TagController) : KoinComponent {

    fun register(app: Javalin) {
        val rolesOptionalAuthenticated = roles(Roles.ANYONE, Roles.AUTHENTICATED)
        app.routes {
            path("users") {
                post({ ctx -> asJson(ctx, authController::register) }, roles(Roles.ANYONE))
                post("login", { ctx -> asJson(ctx, authController::login) }, roles(Roles.ANYONE))
            }
            path("user") {
                get({ ctx -> asJson(ctx, userController::getCurrent) }, roles(Roles.AUTHENTICATED))
                put({ ctx -> asJson(ctx, userController::update) }, roles(Roles.AUTHENTICATED))
            }
            path("profiles/:username") {
                get({ ctx -> asJson(ctx, profileController::get) }, rolesOptionalAuthenticated)
                path("follow") {
                    post({ ctx -> asJson(ctx, profileController::follow) }, roles(Roles.AUTHENTICATED))
                    delete({ ctx -> asJson(ctx, profileController::unfollow) }, roles(Roles.AUTHENTICATED))
                }
            }
            path("articles") {
                get("feed", { ctx -> asJson(ctx, articleController::feed) }, roles(Roles.AUTHENTICATED))
                path(":slug") {
                    path("comments") {
                        post({ ctx -> asJson(ctx, commentController::add) }, roles(Roles.AUTHENTICATED))
                        get({ ctx -> asJson(ctx, commentController::get) }, rolesOptionalAuthenticated)
                        delete(":id", commentController::delete, roles(Roles.AUTHENTICATED))
                    }
                    path("favorite") {
                        post({ ctx -> asJson(ctx, articleController::favorite) }, roles(Roles.AUTHENTICATED))
                        delete({ ctx -> asJson(ctx, articleController::unfavorite) }, roles(Roles.AUTHENTICATED))
                    }
                    get({ ctx -> asJson(ctx, articleController::get) }, rolesOptionalAuthenticated)
                    put({ ctx -> asJson(ctx, articleController::update) }, roles(Roles.AUTHENTICATED))
                    delete(articleController::delete, roles(Roles.AUTHENTICATED))
                }
                get({ ctx -> asJson(ctx, articleController::findBy) }, rolesOptionalAuthenticated)
                post({ ctx -> asJson(ctx, articleController::create) }, roles(Roles.AUTHENTICATED))
            }
            path("tags") {
                get({ ctx -> asJson(ctx, tagController::get) }, roles(Roles.ANYONE))
            }
        }
    }

    private fun asJson(ctx: Context, handler: KFunction1<@ParameterName(name = "ctx") Context, Any>) {
        ctx.json(handler.call(ctx))
    }
}
