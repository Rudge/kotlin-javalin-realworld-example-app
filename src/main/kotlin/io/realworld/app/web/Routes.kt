package io.realworld.app.web

import io.javalin.Context
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.realworld.app.web.controllers.ArticleController
import io.realworld.app.web.controllers.AuthController
import io.realworld.app.web.controllers.CommentController
import io.realworld.app.web.controllers.ProfileController
import io.realworld.app.web.controllers.TagController
import io.realworld.app.web.controllers.UserController
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.reflect.KFunction1

object Routes : KoinComponent {
    private val authController: AuthController by inject()
    private val userController: UserController by inject()
    private val profileController: ProfileController by inject()
    private val articleController: ArticleController by inject()
    private val commentController: CommentController by inject()
    private val tagController: TagController by inject()

    fun register(app: Javalin) {
        app.routes {
            path("users") {
                post { ctx -> asJson(ctx, authController::register) }
                post("login") { ctx -> asJson(ctx, authController::login) }
            }
            path("user") {
                get { ctx -> asJson(ctx, userController::getCurrent) }
                put { ctx -> asJson(ctx, userController::update) }
            }
            path("profiles/:username") {
                get { ctx -> asJson(ctx, profileController::get) }
                path("follow") {
                    post { ctx -> asJson(ctx, profileController::follow) }
                    delete { ctx -> asJson(ctx, profileController::unfollow) }
                }
            }
            path("articles") {
                get("feed") { ctx -> asJson(ctx, articleController::feed) }
                path(":slug") {
                    path("comments") {
                        post { ctx -> asJson(ctx, commentController::add) }
                        get { ctx -> asJson(ctx, commentController::get) }
                        delete(":id", commentController::delete)
                    }
                    path("favorite") {
                        post { ctx -> asJson(ctx, articleController::favorite) }
                        delete { ctx -> asJson(ctx, articleController::unfavorite) }
                    }
                    get { ctx -> asJson(ctx, articleController::get) }
                    put { ctx -> asJson(ctx, articleController::update) }
                    delete(articleController::delete)
                }
                get { ctx -> asJson(ctx, articleController::findBy) }
                post { ctx -> asJson(ctx, articleController::create) }
            }
            path("tags") {
                get { ctx -> asJson(ctx, tagController::get) }
            }
        }
    }

    private fun asJson(ctx: Context, handler: KFunction1<@ParameterName(name = "ctx") Context, Any>) {
        ctx.json(handler.call(ctx))
    }
}
