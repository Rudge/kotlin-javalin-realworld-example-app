package io.realworld.app.domain

import io.javalin.UnauthorizedResponse
import io.realworld.app.config.Roles
import io.realworld.app.domain.repository.UserRepository
import io.realworld.app.utils.Cipher
import io.realworld.app.utils.JwtProvider

data class UserDTO(val user: User)

data class User(val id: Long? = null,
                val email: String,
                val token: String? = null,
                val username: String? = null,
                val password: String,
                val bio: String? = null,
                val image: String? = null)

class UserService(private val jwtProvider: JwtProvider, private val userRepository: UserRepository) {
    fun create(user: User): User {
        userRepository.create(user.copy(password = Cipher.encrypt(user.password)))
        return user
    }

    fun authenticate(user: User): User {
        val userFound = userRepository.findByEmail(user.email)
        if (userFound != null && userFound.password == Cipher.encrypt(user.password)) {
            return userFound.copy(token = jwtProvider.createJWT(userFound, Roles.AUTHENTICATED),
                    password = "")
        }
        throw UnauthorizedResponse("email or password invalid!")
    }
}