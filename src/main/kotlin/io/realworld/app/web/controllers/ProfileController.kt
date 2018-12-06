package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Profile
import io.realworld.app.domain.ProfileDTO

class ProfileController {
    //TODO TEMP
    private val profile = Profile("", "", "", true)

    fun get(ctx: Context): ProfileDTO {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return ProfileDTO(profile)
    }

    fun follow(ctx: Context): ProfileDTO {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return ProfileDTO(profile)
    }

    fun unfollow(ctx: Context): ProfileDTO {
        val username = ctx.validatedPathParam("username").getOrThrow()
        return ProfileDTO(profile.copy(following = false))
    }
}