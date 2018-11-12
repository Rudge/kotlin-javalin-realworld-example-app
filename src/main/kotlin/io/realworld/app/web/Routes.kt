package io.realworld.app.web

import io.javalin.Context
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.realworld.app.domain.Response
import io.realworld.app.web.controllers.ArticleController
import io.realworld.app.web.controllers.AuthController
import io.realworld.app.web.controllers.CommentController
import io.realworld.app.web.controllers.ProfileController
import io.realworld.app.web.controllers.TagController
import io.realworld.app.web.controllers.UserController
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.reflect.KFunction1

class Routes {
    companion object : KoinComponent {
        private val authController: AuthController by inject()
        private val userController: UserController by inject()
        private val profileController: ProfileController by inject()
        private val articleController: ArticleController by inject()
        private val commentController: CommentController by inject()
        private val tagController: TagController by inject()

        fun register(app: Javalin) {
            app.routes {
                path("users") {
                    post { ctx -> asJsonResponse(ctx, authController::register) }
                    post("login") { ctx -> asJsonResponse(ctx, authController::login) }
                }
                path("user") {
                    get { ctx -> asJsonResponse(ctx, userController::getCurrent) }
                    put { ctx -> asJsonResponse(ctx, userController::update) }
                }
                path("profiles/:username") {
                    get { ctx -> asJsonResponse(ctx, profileController::get) }
                    path("follow") {
                        post { ctx -> asJsonResponse(ctx, profileController::follow) }
                        delete { ctx -> asJsonResponse(ctx, profileController::unfollow) }
                    }
                }
                path("articles") {
                    get { ctx -> asJsonResponse(ctx, articleController::findBy) }
                    post { ctx -> asJsonResponse(ctx, articleController::create) }
                    get("feed") { ctx -> asJsonResponse(ctx, articleController::feed) }
                    path(":slug") {
                        get { ctx -> asJsonResponse(ctx, articleController::get) }
                        put { ctx -> asJsonResponse(ctx, articleController::update) }
                        delete(articleController::delete)
                        path("comments") {
                            post { ctx -> asJsonResponse(ctx, commentController::add) }
                            get { ctx -> asJsonResponse(ctx, commentController::get) }
                            delete(":id", commentController::delete)
                        }
                        path("favorite") {
                            post { ctx -> asJsonResponse(ctx, articleController::favorite) }
                            delete { ctx -> asJsonResponse(ctx, articleController::unfavorite) }
                        }
                    }
                }
                path("tags") {
                    get { ctx -> asJsonResponse(ctx, tagController::get) }
                }
            }
        }

        private fun asJsonResponse(ctx: Context, handler: KFunction1<@ParameterName(name = "ctx") Context, Response>) {
            ctx.json(handler.call(ctx))
        }
    }
}