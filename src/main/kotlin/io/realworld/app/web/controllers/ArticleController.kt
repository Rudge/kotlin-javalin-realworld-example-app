package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Article
import io.realworld.app.domain.ArticleDTO
import io.realworld.app.domain.ArticlesDTO
import io.realworld.app.domain.User
import java.util.*

class ArticleController {
    //TODO TEMP
    private val user = User("", "", "", "", "", null)
    private val article = Article("1", "", "", "", listOf(""), Date(), Date(), false, 0, user)

    fun findBy(ctx: Context): ArticlesDTO {
        val tag = ctx.queryParams("tag")
        val author = ctx.queryParams("author")
        val favorited = ctx.queryParams("favorited")
        val limit = ctx.queryParams("limit")
        val offset = ctx.queryParams("offset")
        val articles = listOf(article)
        return ArticlesDTO(articles, articles.size)
    }

    fun feed(ctx: Context): ArticlesDTO {
        val tag = ctx.queryParams("tag")
        val author = ctx.queryParams("author")
        val favorited = ctx.queryParams("favorited")
        val limit = ctx.queryParams("limit")
        val offset = ctx.queryParams("offset")
        val articles = listOf(article)
        return ArticlesDTO(articles, articles.size)
    }

    fun get(ctx: Context): ArticleDTO {
        val slug = ctx.validatedPathParam("slug")
        return ArticleDTO(article)
    }

    fun create(ctx: Context): ArticleDTO {
        val articleRequest = ctx.validatedBody<ArticleDTO>().getOrThrow()
        articleRequest.article.createdAt = Date()
        articleRequest.article.updatedAt = Date()
        return ArticleDTO(article)
    }

    fun update(ctx: Context): ArticleDTO {
        val slug = ctx.validatedPathParam("slug")
        return ArticleDTO(article)
    }

    fun delete(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
    }

    fun favorite(ctx: Context): ArticleDTO {
        val slug = ctx.validatedPathParam("slug")
        return ArticleDTO(article.copy(favorited = true, favoritesCount = 1))
    }

    fun unfavorite(ctx: Context): ArticleDTO {
        val slug = ctx.validatedPathParam("slug")
        return ArticleDTO(article)
    }
}