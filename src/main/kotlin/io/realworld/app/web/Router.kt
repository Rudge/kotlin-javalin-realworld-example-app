package io.realworld.app.web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.javalin.core.security.SecurityUtil.roles
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
    private val tagController: TagController
) : KoinComponent {

    fun register(app: Javalin) {
        val rolesOptionalAuthenticated = roles(Roles.ANYONE, Roles.AUTHENTICATED)
        app.routes {
            path("users") {
                post(userController::register, roles(Roles.ANYONE))
                post("login", userController::login, roles(Roles.ANYONE))
            }
            path("user") {
                get(userController::getCurrent, roles(Roles.AUTHENTICATED))
                put(userController::update, roles(Roles.AUTHENTICATED))
            }
            path("profiles/:username") {
                get(profileController::get, rolesOptionalAuthenticated)
                path("follow") {
                    post(profileController::follow, roles(Roles.AUTHENTICATED))
                    delete(profileController::unfollow, roles(Roles.AUTHENTICATED))
                }
            }
            path("articles") {
                get("feed", articleController::feed, roles(Roles.AUTHENTICATED))
                path(":slug") {
                    path("comments") {
                        post(commentController::add, roles(Roles.AUTHENTICATED))
                        get(commentController::findBySlug, rolesOptionalAuthenticated)
                        delete(":id", commentController::delete, roles(Roles.AUTHENTICATED))
                    }
                    path("favorite") {
                        post(articleController::favorite, roles(Roles.AUTHENTICATED))
                        delete(articleController::unfavorite, roles(Roles.AUTHENTICATED))
                    }
                    get(articleController::get, rolesOptionalAuthenticated)
                    put(articleController::update, roles(Roles.AUTHENTICATED))
                    delete(articleController::delete, roles(Roles.AUTHENTICATED))
                }
                get(articleController::findBy, rolesOptionalAuthenticated)
                post(articleController::create, roles(Roles.AUTHENTICATED))
            }
            path("tags") {
                get(tagController::get, rolesOptionalAuthenticated)
            }
        }
    }
}
