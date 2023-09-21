package io.realworld.app.web.controllers

import io.javalin.http.Context
import io.realworld.app.domain.UserDTO
import io.realworld.app.domain.service.UserService
import io.realworld.app.ext.isEmailValid

class UserController(private val userService: UserService) {
    fun login(ctx: Context) {
        ctx.bodyValidator<UserDTO>()
            .check({ it.user?.email?.isEmailValid() ?: true }, "Email is not valid")
            .check({ !it.user?.password.isNullOrBlank() }, "Password is empty")
            .get().user?.also { user ->
            userService.authenticate(user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }

    fun register(ctx: Context) {
        ctx.bodyValidator<UserDTO>()
            .check({ !it.user?.email.isNullOrBlank() }, "Email is empty")
            .check({ it.user?.email?.isEmailValid() ?: true }, "Email is not valid")
            .check({ !it.user?.password.isNullOrBlank() }, "Password is empty")
            .check({ !it.user?.username.isNullOrBlank() }, "Username is empty")
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
            .check({ it.user != null }, "User must not be null")
            .check({ it.user?.email?.isEmailValid() ?: true }, "Email must be valid")
            .check({ it.user?.username?.isNotBlank() ?: true }, "username must be not empty")
            .check({ it.user?.password?.isNotBlank() ?: true }, "password must be not empty")
            .check({ it.user?.bio?.isNotBlank() ?: true }, "bio must be not empty")
            .check({ it.user?.image?.isNotBlank() ?: true }, "image must be not empty")
            .get()
            .user?.also { user ->
            userService.update(email, user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }
}
