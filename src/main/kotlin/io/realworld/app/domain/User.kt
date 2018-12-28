package io.realworld.app.domain

import io.javalin.UnauthorizedResponse
import io.realworld.app.config.Roles
import io.realworld.app.domain.repository.UserRepository
import io.realworld.app.utils.Cipher
import io.realworld.app.utils.JwtProvider

data class UserDTO(val user: User? = null)

data class User(val id: Long? = null,
                val email: String,
                val token: String? = null,
                val username: String? = null,
                val password: String,
                val bio: String? = null,
                val image: String? = null)

class UserService(private val jwtProvider: JwtProvider, private val userRepository: UserRepository) {
    fun create(user: User): User {
        val id = userRepository.create(user.copy(password = Cipher.encrypt(user.password)))
        return user.copy(id = id)
    }

    fun authenticate(user: User): User {
        val userFound = userRepository.findByEmail(user.email)
        if (userFound?.password == Cipher.encrypt(user.password)) {
            return userFound.copy(token = generateJwtToken(userFound),
                    password = "")
        }
        throw UnauthorizedResponse("email or password invalid!")
    }

    fun getCurrent(email: String?): User? {
        if (email.isNullOrBlank()) return null
        val user = userRepository.findByEmail(email)
        return user?.copy(token = generateJwtToken(user))
    }

    fun update(user: User): User? {
        return userRepository.update(user)
    }

    private fun generateJwtToken(user: User): String? {
        return jwtProvider.createJWT(user, Roles.AUTHENTICATED)
    }
}