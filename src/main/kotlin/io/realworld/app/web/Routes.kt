package io.realworld.app.web

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
                path("/api/users") {
                    post("login", authController::login)
                    post(authController::register)
                }
                path("/api/user") {
                    get(userController::getCurrent)
                    put(userController::getCurrent)
                }
                path("/api/profiles/:username") {
                    get(profileController::get)
                    path("follow") {
                        post(profileController::follow)
                        delete(profileController::unfollow)
                    }
                }
                path("/api/articles") {
                    get(articleController::findBy)
                    post(articleController::create)
                    get("feed", articleController::feed)
                    path(":slug") {
                        get(articleController::feed)
                        put(articleController::update)
                        delete(articleController::delete)
                        path("comments") {
                            post(commentController::add)
                            get(commentController::get)
                            delete(":id", commentController::delete)
                        }
                        path("favorite") {
                            post(articleController::favorite)
                            delete(articleController::unfavorite)
                        }
                    }
                }
                path("/api/tags") {
                    get(tagController::get)
                }
            }
        }
    }
}