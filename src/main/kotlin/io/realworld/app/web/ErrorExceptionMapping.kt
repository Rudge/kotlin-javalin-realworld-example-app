package io.realworld.app.web

import com.fasterxml.jackson.annotation.JsonRootName
import io.javalin.BadRequestResponse
import io.javalin.Context
import io.javalin.Javalin
import io.realworld.app.exception.HttpResponseException
import org.eclipse.jetty.http.HttpStatus

@JsonRootName("errors")
class ErrorResponse : HashMap<String, Any>() {
    fun toJson(ctx: Context) {
        ctx.json(this)
    }
}

object ErrorExceptionMapping {
    fun register(app: Javalin) {
        app.exception(Exception::class.java) { e, ctx ->
            val error = ErrorResponse()
            error["Unknow Error"] = listOf(e.message)
            ctx.json(error)
        }
        app.exception(BadRequestResponse::class.java) { e, ctx ->
            val error = ErrorResponse()
            error["body"] = listOf(e.msg)
            ctx.status(HttpStatus.UNPROCESSABLE_ENTITY_422).json(error)
        }
        app.exception(HttpResponseException::class.java) { e, ctx ->
            val error = ErrorResponse()
            error[e.field] = listOf(e.msg)
            ctx.status(e.status).json(error)
        }
    }
}