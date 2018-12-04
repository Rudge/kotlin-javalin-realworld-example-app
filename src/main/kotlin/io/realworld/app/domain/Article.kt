package io.realworld.app.domain

import java.util.*

data class ArticleDTO(val article: Article)

data class ArticlesDTO(val articles: List<Article>, val articlesCount: Int)

data class Article(val slug: String?,
                   val title: String,
                   val description: String,
                   val body: String,
                   val tagList: List<String>,
                   var createdAt: Date?,
                   var updatedAt: Date?,
                   val favorited: Boolean,
                   val favoritesCount: Long?,
                   val author: User?)