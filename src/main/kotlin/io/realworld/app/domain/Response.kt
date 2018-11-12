package io.realworld.app.domain

import com.fasterxml.jackson.annotation.JsonRootName
import io.javalin.Context

class Response(private val rootName: String, private val obj: Any) : HashMap<String, Any>() {
    init {
        this[rootName] = obj
    }

    fun also(rootName: String, obj: Any): Response {
        this[rootName] = obj
        return this
    }
}

@JsonRootName("errors")
class ErrorResponse : HashMap<String, List<String?>>() {
    fun toJson(ctx: Context) {
        ctx.json(this)
    }
}