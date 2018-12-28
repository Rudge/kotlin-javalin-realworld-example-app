package io.realworld.app.domain

import io.realworld.app.domain.repository.ArticleRepository
import io.realworld.app.domain.repository.UserRepository
import java.util.*

data class ArticleDTO(val article: Article)

data class ArticlesDTO(val articles: List<Article>, val articlesCount: Int)

data class Article(val slug: String? = null,
                   val title: String,
                   val description: String,
                   val body: String,
                   val tagList: List<String> = listOf(),
                   var createdAt: Date? = null,
                   var updatedAt: Date? = null,
                   val favorited: Boolean = false,
                   val favoritesCount: Long? = null,
                   val author: User? = null)

class ArticleService(private val articleRepository: ArticleRepository,
                     private val userRepository: UserRepository) {

    fun findBy(tag: String?, author: String?, favorited: String?, limitParam: Int?, offsetParam: Int?):
            List<Article> {
        val limit = limitParam ?: 0
        val offset = offsetParam ?: 0
        return when {
            !tag.isNullOrBlank() -> articleRepository.findByTag(tag, limit, offset)
            !author.isNullOrBlank() -> articleRepository.findByAuthor(author, limit, offset)
            !favorited.isNullOrBlank() -> articleRepository.findByFavorited(favorited, limit, offset)
            else -> articleRepository.findAll(limit, offset)
        }
    }

    fun create(email: String?, article: Article): ArticleDTO {
        if (email == null) {
            throw IllegalArgumentException("invalid user to create article")
        }
        val author = userRepository.findByEmail(email)
        articleRepository.create(article.copy(author = author))
        return ArticleDTO(article)
    }
}