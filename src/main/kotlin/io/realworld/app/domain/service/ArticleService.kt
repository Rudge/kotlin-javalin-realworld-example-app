package io.realworld.app.domain.service

import com.github.slugify.Slugify
import io.javalin.HttpResponseException
import io.javalin.NotFoundResponse
import io.realworld.app.domain.Article
import io.realworld.app.domain.repository.ArticleRepository
import io.realworld.app.domain.repository.UserRepository
import org.eclipse.jetty.http.HttpStatus

class ArticleService(private val articleRepository: ArticleRepository,
                     private val userRepository: UserRepository) {

    fun findBy(tag: String?, author: String?, favorited: String?, limit: Int, offset: Int):
            List<Article> {
        return when {
            !tag.isNullOrBlank() -> articleRepository.findByTag(tag!!, limit, offset)
            !author.isNullOrBlank() -> articleRepository.findByAuthor(author!!, limit, offset)
            !favorited.isNullOrBlank() -> articleRepository.findByFavorited(favorited!!, limit, offset)
            else -> articleRepository.findAll(limit, offset)
        }
    }

    fun create(email: String?, article: Article): Article {
        email ?: throw IllegalArgumentException("invalid user to create article")
        val author = userRepository.findByEmail(email)
        val articleCreated = articleRepository.create(article.copy(slug = Slugify().slugify(article.title), author =
        author))
        articleCreated ?: throw HttpResponseException(HttpStatus.NOT_ACCEPTABLE_406, "Article not found to update.")
        return articleCreated
    }

    fun findBySlug(slug: String): Article? {
        return articleRepository.findBySlug(slug) ?: throw NotFoundResponse()
    }

    fun update(slug: String, article: Article): Article? {
        return articleRepository.update(slug, article.copy(slug = Slugify().slugify(article.title)))
    }

    fun findFeed(limit: Int, offset: Int): List<Article> {
        return articleRepository.findAll(limit, offset)
    }

    fun favorite(slug: String): Article {
        return articleRepository.favorite(slug)
    }

    fun unfavorite(slug: String): Article {
        return articleRepository.unfavorite(slug)
    }

    fun delete(slug: String): Article {
        return articleRepository.delete(slug)
    }
}