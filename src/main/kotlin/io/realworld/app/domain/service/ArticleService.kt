package io.realworld.app.domain.service

import com.github.slugify.Slugify
import io.javalin.http.BadRequestResponse
import io.javalin.http.InternalServerErrorResponse
import io.javalin.http.NotFoundResponse
import io.realworld.app.domain.Article
import io.realworld.app.domain.repository.ArticleRepository
import io.realworld.app.domain.repository.UserRepository

class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
) {

    fun findBy(tag: String?, author: String?, favorited: String?, limit: Int, offset: Int):
        List<Article> {
        return when {
            !tag.isNullOrBlank() -> articleRepository.findByTag(tag, limit, offset)
            !author.isNullOrBlank() -> articleRepository.findByAuthor(author, limit, offset)
            !favorited.isNullOrBlank() -> articleRepository.findByFavorited(favorited, limit, offset)
            else -> articleRepository.findAll(limit, offset)
        }
    }

    fun create(email: String?, article: Article): Article {
        email ?: throw BadRequestResponse("invalid user to create article")
        return userRepository.findByEmail(email).let { author ->
            author ?: throw BadRequestResponse("invalid user to create article")
            articleRepository.create(
                article.copy(slug = Slugify().slugify(article.title), author = author),
            )
                ?: throw InternalServerErrorResponse("Error to create article.")
        }
    }

    fun findBySlug(slug: String): Article? {
        return articleRepository.findBySlug(slug) ?: throw NotFoundResponse()
    }

    fun update(slug: String, article: Article): Article? {
        return findBySlug(slug).run {
            articleRepository.update(slug, article.copy(slug = slug))
        }
    }

    fun findFeed(email: String?, limit: Int, offset: Int): List<Article> {
        email ?: throw BadRequestResponse("invalid user to find feeds")
        return articleRepository.findFeed(email, limit, offset)
    }

    fun favorite(email: String?, slug: String): Article {
        email ?: throw BadRequestResponse("invalid user to favorite article")
        val article = findBySlug(slug) ?: throw NotFoundResponse()
        return userRepository.findByEmail(email).let { user ->
            user ?: throw BadRequestResponse()
            articleRepository.favorite(user.id!!, slug)
                .let { favoritesCount ->
                    article.copy(favorited = true, favoritesCount = favoritesCount.toLong())
                }
        }
    }

    fun unfavorite(email: String?, slug: String): Article {
        email ?: throw BadRequestResponse("invalid user to unfavorite article")
        val article = findBySlug(slug) ?: throw NotFoundResponse()
        return userRepository.findByEmail(email).let { user ->
            user ?: throw BadRequestResponse()
            articleRepository.unfavorite(user.id!!, slug)
                .let { favoritesCount ->
                    article.copy(favorited = false, favoritesCount = favoritesCount.toLong())
                }
        }
    }

    fun delete(slug: String) {
        return articleRepository.delete(slug)
    }
}
