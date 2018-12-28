package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.Article
import io.realworld.app.domain.ArticleDTO
import io.realworld.app.domain.ArticleService
import io.realworld.app.domain.ArticlesDTO
import io.realworld.app.domain.User
import java.util.*

class ArticleController(private val articleService: ArticleService) {
    //TODO TEMP
    private val user = User(0, "", "", "", "", "", null)
    private val article = Article("1", "", "", "", listOf(""), Date(), Date(), false, 0, user)

    fun findBy(ctx: Context) {
        val tag = ctx.queryParam("tag")
        val author = ctx.queryParam("author")
        val favorited = ctx.queryParam("favorited")
        val limit = ctx.queryParam("limit")
        val offset = ctx.queryParam("offset")
        //val articles = listOf(article.copy(tagList = listOf(tag ?: ""), author = user.copy(username = author)))
        val articles = articleService.findBy(tag, author, favorited, limit?.toInt(), offset?.toInt())
        ctx.json(ArticlesDTO(articles, articles.size))
    }

    fun feed(ctx: Context) {
        val tag = ctx.queryParam("tag")
        val author = ctx.queryParam("author")
        val favorited = ctx.queryParam("favorited")
        val limit = ctx.queryParam("limit")
        val offset = ctx.queryParam("offset")
        val articles = listOf(article)
        ctx.json(ArticlesDTO(articles, articles.size))
    }

    fun get(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        ctx.json(ArticleDTO(article))
    }

    fun create(ctx: Context) {
        val articleRequest = ctx
                .validatedBody<ArticleDTO>()
                .check({ !it.article.title.isNullOrBlank() })
                .check({ !it.article.description.isNullOrBlank() })
                .check({ !it.article.body.isNullOrBlank() })
                .getOrThrow()
        articleRequest.article.createdAt = Date()
        articleRequest.article.updatedAt = Date()
        ctx.json(ArticleDTO(articleService.create(ctx.attribute("email"), articleRequest.article)))
    }

    fun update(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        val articleRequest = ctx.validatedBody<ArticleDTO>()
                .check({ it.article.title.isNotBlank() })
                .check({ it.article.description.isNotBlank() })
                .check({ !it.article.body.isNullOrBlank() })
                .getOrThrow()
        ctx.json(ArticleDTO(article.copy(body = articleRequest.article.body)))
    }

    fun delete(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
    }

    fun favorite(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        ctx.json(ArticleDTO(article.copy(favorited = true, favoritesCount = 1)))
    }

    fun unfavorite(ctx: Context) {
        val slug = ctx.validatedPathParam("slug")
        ctx.json(ArticleDTO(article))
    }
}