package io.realworld.app.utils

import com.auth0.jwt.algorithms.Algorithm
import java.nio.charset.Charset

object Cipher {
    val algorithm = Algorithm.HMAC256("something-very-secret-here")

    fun encrypt(data: String?): String =
            String(algorithm.sign(data?.toByteArray()), Charset.forName("UTF-8"))

}