package io.realworld.app.domain

import java.util.*

data class ArticleRequest(val article: Article)

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