package io.realworld.app.web

import io.javalin.Javalin
import io.realworld.app.domain.ErrorResponse
import io.realworld.app.exception.HttpResponseException

class ErrorExceptionMapping {

    companion object {
        fun register(app: Javalin) {
            app.exception(Exception::class.java) { e, ctx ->
                val error = ErrorResponse()
                error["Unknow Error"] = listOf(e.message)
                ctx.json(error)
            }
            app.exception(HttpResponseException::class.java) { e, ctx ->
                val error = ErrorResponse()
                error[e.field] = listOf(e.msg)
                ctx.status(e.status).json(error)
            }
        }
    }
}