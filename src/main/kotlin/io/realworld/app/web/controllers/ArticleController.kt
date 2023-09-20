package io.realworld.app.web.controllers

import io.javalin.http.Context
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
        articleService.findFeed(ctx.attribute("email"), limit.toInt(), offset.toInt()).also { articles ->
            ctx.json(ArticlesDTO(articles, articles.size))
        }
    }

    fun get(ctx: Context) {
        ctx.pathParamAsClass<String>("slug")
            .check({ it.isNotBlank() }, "slug must not be blank")
            .get().also { slug ->
                articleService.findBySlug(slug).apply {
                    ctx.json(ArticleDTO(this))
                }
            }
    }

    fun create(ctx: Context) {
        ctx.bodyValidator<ArticleDTO>()
            .check({ !it.article?.title.isNullOrBlank() }, "Title is null")
            .check({ !it.article?.description.isNullOrBlank() }, "Description is null")
            .check({ !it.article?.body.isNullOrBlank() }, "Body is null")
            .get().article?.also { article ->
            articleService.create(ctx.attribute("email"), article).apply {
                ctx.json(ArticleDTO(this))
            }
        }
    }

    fun update(ctx: Context) {
        val slug = ctx.pathParam("slug")
        ctx.bodyValidator<ArticleDTO>()
            .check({ !it.article?.body.isNullOrBlank() }, "Body is null")
            .get().article?.also { article ->
            articleService.update(slug, article).apply {
                ctx.json(ArticleDTO(this))
            }
        }
    }

    fun delete(ctx: Context) {
        ctx.pathParam("slug").also { slug ->
            articleService.delete(slug)
        }
    }

    fun favorite(ctx: Context) {
        ctx.pathParam("slug").also { slug ->
            articleService.favorite(ctx.attribute("email"), slug).apply {
                ctx.json(ArticleDTO(this))
            }
        }
    }

    fun unfavorite(ctx: Context) {
        ctx.pathParam("slug").also { slug ->
            articleService.unfavorite(ctx.attribute("email"), slug).apply {
                ctx.json(ArticleDTO(this))
            }
        }
    }
}
