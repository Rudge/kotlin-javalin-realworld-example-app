package io.realworld.app.web.controllers

import io.javalin.http.Context
import io.realworld.app.domain.UserDTO
import io.realworld.app.domain.service.UserService
import io.realworld.app.ext.isEmailValid

class UserController(private val userService: UserService) {
    fun login(ctx: Context) {
        ctx.bodyValidator<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ !it.user?.password.isNullOrBlank() })
                .get().user?.also { user ->
            userService.authenticate(user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }

    fun register(ctx: Context) {
        ctx.bodyValidator<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ !it.user?.password.isNullOrBlank() })
                .check({ !it.user?.username.isNullOrBlank() })
                .get().user?.also { user ->
            userService.create(user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }

    fun getCurrent(ctx: Context) {
        userService.getByEmail(ctx.attribute("email")).also { user ->
            ctx.json(UserDTO(user))
        }
    }

    fun update(ctx: Context) {
        val email = ctx.attribute<String>("email")
        ctx.bodyValidator<UserDTO>()
                .check({ it.user != null })
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ it.user?.username?.isNotBlank() ?: true })
                .check({ it.user?.password?.isNotBlank() ?: true })
                .check({ it.user?.bio?.isNotBlank() ?: true })
                .check({ it.user?.image?.isNotBlank() ?: true })
                .get()
                .user?.also { user ->
            userService.update(email, user).apply {
                ctx.json(UserDTO(this))
            }
        }

    }
}