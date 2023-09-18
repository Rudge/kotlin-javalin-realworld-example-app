package io.realworld.app.web.controllers

import io.javalin.http.Context
import io.realworld.app.domain.ProfileDTO
import io.realworld.app.domain.service.UserService

class ProfileController(private val userService: UserService) {
    fun get(ctx: Context) {
        ctx.pathParam<String>("username").get().also { usernameFollowing ->
            userService.getProfileByUsername(ctx.attribute("email")!!, usernameFollowing).also { profile ->
                ctx.json(ProfileDTO(profile))
            }
        }
    }

    fun follow(ctx: Context) {
        ctx.pathParam<String>("username").get().also { usernameToFollow ->
            userService.follow(ctx.attribute("email")!!, usernameToFollow).also { profile ->
                ctx.json(ProfileDTO(profile))
            }
        }
    }

    fun unfollow(ctx: Context) {
        ctx.pathParam<String>("username").get().also { usernameToUnfollow ->
            userService.unfollow(ctx.attribute("email")!!, usernameToUnfollow).also { profile ->
                ctx.json(ProfileDTO(profile))
            }
        }
    }
}