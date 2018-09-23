package io.realworld.app.domain

import java.util.*

data class Comment(val id: Long,
                   val createdAt: Date,
                   val updatedAt: Date,
                   val body: String,
                   val author: User)