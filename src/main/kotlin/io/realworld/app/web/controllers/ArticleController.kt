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
        val tag = ctx.queryParam("tag")
        val author = ctx.queryParam("author")
        val favorited = ctx.queryParam("favorited")
        val limit = ctx.queryParam("limit")
        val offset = ctx.queryParam("offset")
        val articles = listOf(article.copy(tagList = listOf(tag ?: ""), author = user.copy(username = author)))
        return ArticlesDTO(articles, articles.size)
    }

    fun feed(ctx: Context): ArticlesDTO {
        val tag = ctx.queryParam("tag")
        val author = ctx.queryParam("author")
        val favorited = ctx.queryParam("favorited")
        val limit = ctx.queryParam("limit")
        val offset = ctx.queryParam("offset")
        val articles = listOf(article)
        return ArticlesDTO(articles, articles.size)
    }

    fun get(ctx: Context): ArticleDTO {
        val slug = ctx.validatedPathParam("slug")
        return ArticleDTO(article)
    }

    fun create(ctx: Context): ArticleDTO {
        val articleRequest = ctx
                .validatedBody<ArticleDTO>()
                .check({ !it.article.title.isNullOrBlank() })
                .check({ !it.article.description.isNullOrBlank() })
                .check({ !it.article.body.isNullOrBlank() })
                .getOrThrow()
        articleRequest.article.createdAt = Date()
        articleRequest.article.updatedAt = Date()
        return ArticleDTO(article.copy(title = articleRequest.article.title, description = articleRequest.article
                .description, body = articleRequest.article.body, tagList = articleRequest.article.tagList))
    }

    fun update(ctx: Context): ArticleDTO {
        val slug = ctx.validatedPathParam("slug")
        val articleRequest = ctx.validatedBody<ArticleDTO>()
                .check({ it.article.title?.isNotBlank() ?: true })
                .check({ it.article.description?.isNotBlank() ?: true })
                .check({ !it.article.body.isNullOrBlank() })
                .getOrThrow()
        return ArticleDTO(article.copy(body = articleRequest.article.body))
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