package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Profile
import io.realworld.app.domain.ProfileDTO

class ProfileController {
    //TODO TEMP
    private val profile = Profile("", "", "", true)

    fun get(ctx: Context) {
        val username = ctx.validatedPathParam("username").getOrThrow()
        ctx.json(ProfileDTO(profile.copy(username = username)))
    }

    fun follow(ctx: Context) {
        val username = ctx.validatedPathParam("username").getOrThrow()
        ctx.json(ProfileDTO(profile.copy(username = username)))
    }

    fun unfollow(ctx: Context) {
        val username = ctx.validatedPathParam("username").getOrThrow()
        ctx.json(ProfileDTO(profile.copy(username = username, following = false)))
    }
}