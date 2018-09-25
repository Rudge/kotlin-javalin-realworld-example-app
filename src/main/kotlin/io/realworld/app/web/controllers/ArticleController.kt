package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Article
import io.realworld.app.domain.User
import java.util.*

class ArticleController {

    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)
    private val article = Article("", "", "", "", listOf(""), Date(), Date(), false, 0, user)

    fun findBy(ctx: Context) {
        val tag = ctx.queryParams("tag")
        val author = ctx.queryParams("author")
        val favorited = ctx.queryParams("favorited")
        val limit = ctx.queryParams("limit")
        val offset = ctx.queryParams("offset")
        ctx.json(listOf(article))
    }

    fun feed(ctx: Context) {
        val tag = ctx.queryParams("tag")
        val author = ctx.queryParams("author")
        val favorited = ctx.queryParams("favorited")
        val limit = ctx.queryParams("limit")
        val offset = ctx.queryParams("offset")
        ctx.json(listOf(article))
    }

    fun get(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        ctx.json(article)
    }

    fun create(ctx: Context) {
        val article = ctx.validatedBody<Article>()
        ctx.json(article)
    }

    fun update(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        ctx.json(article)
    }

    fun delete(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
    }

    fun favorite(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        ctx.json(article)
    }

    fun unfavorite(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        ctx.json(article)
    }
}