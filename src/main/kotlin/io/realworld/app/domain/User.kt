package io.realworld.app.domain

data class UserDTO(val user: User)

data class User(val email: String? = null,
                val token: String? = null,
                val username: String? = null,
                val password: String? = null,
                val bio: String? = null,
                val image: String? = null)