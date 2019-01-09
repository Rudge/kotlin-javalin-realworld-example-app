package io.realworld.app.web.controllers

import io.javalin.Context
import io.realworld.app.domain.ArticleDTO
import io.realworld.app.domain.ArticlesDTO
import io.realworld.app.domain.service.ArticleService

class ArticleController(private val articleService: ArticleService) {
    fun findBy(ctx: Context) {
        val tag = ctx.queryParam("tag")
        val author = ctx.queryParam("author")
        val favorited = ctx.queryParam("favorited")
        val limit = ctx.queryParam("limit") ?: "20"
        val offset = ctx.queryParam("offset") ?: "0"
        articleService.findBy(tag, author, favorited, limit.toInt(), offset.toInt()).also { articles ->
            ctx.json(ArticlesDTO(articles, articles.size))
        }
    }

    fun feed(ctx: Context) {
        val limit = ctx.queryParam("limit") ?: "20"
        val offset = ctx.queryParam("offset") ?: "0"
        articleService.findFeed(limit.toInt(), offset.toInt()).also { articles ->
            ctx.json(ArticlesDTO(articles, articles.size))
        }
    }

    fun get(ctx: Context) {
        ctx.validatedPathParam("slug")
                .check({ it.isNotBlank() })
                .getOrThrow().also { slug ->
                    articleService.findBySlug(slug).apply {
                        ctx.json(ArticleDTO(this))
                    }
                }
    }

    fun create(ctx: Context) {
        ctx.validatedBody<ArticleDTO>()
                .check({ !it.article?.title.isNullOrBlank() })
                .check({ !it.article?.description.isNullOrBlank() })
                .check({ !it.article?.body.isNullOrBlank() })
                .getOrThrow().article?.also { article ->
            articleService.create(ctx.attribute("email"), article).apply {
                ctx.json(ArticleDTO(this))
            }
        }
    }

    fun update(ctx: Context) {
        val slug = ctx.validatedPathParam("slug").getOrThrow()
        ctx.validatedBody<ArticleDTO>()
                .check({ !it.article?.body.isNullOrBlank() })
                .getOrThrow().article?.also { article ->
            articleService.update(slug, article).apply {
                ctx.json(ArticleDTO(this))
            }
        }
    }

    fun delete(ctx: Context) {
        ctx.validatedPathParam("slug").getOrThrow().also { slug ->
            articleService.delete(slug)
        }
    }

    fun favorite(ctx: Context) {
        ctx.validatedPathParam("slug").getOrThrow().also { slug ->
            articleService.favorite(slug).apply {
                ctx.json(ArticleDTO(this))
            }
        }
    }

    fun unfavorite(ctx: Context) {
        ctx.validatedPathParam("slug").getOrThrow().also { slug ->
            articleService.unfavorite(slug).apply {
                ctx.json(ArticleDTO(this))
            }
        }
    }
}