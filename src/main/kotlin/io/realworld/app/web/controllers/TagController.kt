package io.realworld.app.web.controllers

import io.javalin.http.Context
import io.realworld.app.domain.service.TagService

class TagController(private val tagService: TagService) {
    fun get(ctx: Context) {
        tagService.findAll().also { tagDto ->
            ctx.json(tagDto)
        }
    }
}
