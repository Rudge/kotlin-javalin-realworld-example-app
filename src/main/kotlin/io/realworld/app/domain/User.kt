package io.realworld.app.domain

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("user")
data class User(val email: String,
                val token: String,
                val username: String,
                val bio: String,
                val image: String?,
                val following: Boolean)