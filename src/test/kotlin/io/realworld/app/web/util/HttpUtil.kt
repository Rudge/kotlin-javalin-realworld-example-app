/*
 * Javalin - https://javalin.io
 * Copyright 2017 David Åse
 * Licensed under Apache 2.0: https://github.com/tipsy/javalin/blob/master/LICENSE
 */

package io.javalin.util

import com.mashape.unirest.http.ObjectMapper
import com.mashape.unirest.http.Unirest
import io.javalin.Javalin
import io.javalin.core.util.Header
import io.javalin.json.JavalinJson

class HttpUtil(javalin: Javalin) {

    init {
        Unirest.setObjectMapper(object : ObjectMapper {
            override fun <T> readValue(value: String, valueType: Class<T>): T {
                return JavalinJson.fromJson(value, valueType)
            }

            override fun writeValue(value: Any): String {
                return return JavalinJson.toJson(value)
            }
        })
    }

    @JvmField
    val origin: String = "http://localhost:" + javalin.port()

    inline fun <reified T> post(path: String) =
            Unirest.post(origin + path).header(Header.ACCEPT,
                    "application/json").asObject(T::class.java)

    inline fun <reified T> post(path: String, body: Any) =
            Unirest.post(origin + path).header(Header.ACCEPT,
                    "application/json").body(body).asObject(T::class.java)

    inline fun <reified T> get(path: String) = Unirest.get(origin + path).header(Header.ACCEPT,
            "application/json").asObject(T::class.java)
}