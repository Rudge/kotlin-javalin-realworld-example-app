package io.realworld.app.web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.realworld.app.config.Roles
import io.realworld.app.web.controllers.ArticleController
import io.realworld.app.web.controllers.CommentController
import io.realworld.app.web.controllers.ProfileController
import io.realworld.app.web.controllers.TagController
import io.realworld.app.web.controllers.UserController
import org.koin.standalone.KoinComponent

class Router(
    private val userController: UserController,
    private val profileController: ProfileController,
    private val articleController: ArticleController,
    private val commentController: CommentController,
    private val tagController: TagController,
) : KoinComponent {

    fun register(app: Javalin) {
        app.routes {
            path("users") {
                post(userController::register, Roles.ANYONE)
                post("login", userController::login, Roles.ANYONE)
            }
            path("user") {
                get(userController::getCurrent, Roles.AUTHENTICATED)
                put(userController::update, Roles.AUTHENTICATED)
            }
            path("profiles/{username}") {
                get(profileController::get, Roles.ANYONE, Roles.AUTHENTICATED)
                path("follow") {
                    post(profileController::follow, Roles.AUTHENTICATED)
                    delete(profileController::unfollow, Roles.AUTHENTICATED)
                }
            }
            path("articles") {
                get("feed", articleController::feed, Roles.AUTHENTICATED)
                path("{slug}") {
                    path("comments") {
                        post(commentController::add, Roles.AUTHENTICATED)
                        get(commentController::findBySlug, Roles.ANYONE, Roles.AUTHENTICATED)
                        delete("{id}", commentController::delete, Roles.AUTHENTICATED)
                    }
                    path("favorite") {
                        post(articleController::favorite, Roles.AUTHENTICATED)
                        delete(articleController::unfavorite, Roles.AUTHENTICATED)
                    }
                    get(articleController::get, Roles.ANYONE, Roles.AUTHENTICATED)
                    put(articleController::update, Roles.AUTHENTICATED)
                    delete(articleController::delete, Roles.AUTHENTICATED)
                }
                get(articleController::findBy, Roles.ANYONE, Roles.AUTHENTICATED)
                post(articleController::create, Roles.AUTHENTICATED)
            }
            path("tags") {
                get(tagController::get, Roles.ANYONE, Roles.AUTHENTICATED)
            }
        }
    }
}
