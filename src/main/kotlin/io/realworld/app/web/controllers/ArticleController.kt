package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Article
import io.realworld.app.domain.ArticleRequest
import io.realworld.app.domain.Response
import io.realworld.app.domain.User
import java.util.*

class ArticleController {
    //TODO TEMP
    private val user = User("", "", "", "", "", null, true)
    private val article = Article("", "", "", "", listOf(""), Date(), Date(), false, 0, user)

    fun findBy(ctx: Context): Response {
        val tag = ctx.queryParams("tag")
        val author = ctx.queryParams("author")
        val favorited = ctx.queryParams("favorited")
        val limit = ctx.queryParams("limit")
        val offset = ctx.queryParams("offset")
        val articles = listOf(article)
        return Response("articles", articles).also("articlesCount", articles.size)
    }

    fun feed(ctx: Context): Response {
        val tag = ctx.queryParams("tag")
        val author = ctx.queryParams("author")
        val favorited = ctx.queryParams("favorited")
        val limit = ctx.queryParams("limit")
        val offset = ctx.queryParams("offset")
        val articles = listOf(article)
        return Response("articles", articles).also("articlesCount", articles.size)
    }

    fun get(ctx: Context): Response {
        val slug = ctx.validatedPathParam("slug")
        return Response("article", article)
    }

    fun create(ctx: Context): Response {
        val articleRequest = ctx.validatedBody<ArticleRequest>().getOrThrow()
        articleRequest.article.createdAt = Date()
        articleRequest.article.updatedAt = Date()
        return Response("article", article)
    }

    fun update(ctx: Context): Response {
        val slug = ctx.validatedPathParam("slug")
        return Response("article", article)
    }

    fun delete(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
    }

    fun favorite(ctx: Context): Response {
        val slug = ctx.validatedPathParam("slug")
        return Response("article", article)
    }

    fun unfavorite(ctx: Context): Response {
        val slug = ctx.validatedPathParam("slug")
        return Response("article", article)
    }
}