package io.realworld.app.web

import com.fasterxml.jackson.annotation.JsonRootName
import io.javalin.BadRequestResponse
import io.javalin.HttpResponseException
import io.javalin.Javalin
import io.javalin.UnauthorizedResponse
import org.eclipse.jetty.http.HttpStatus

@JsonRootName("errors")
class ErrorResponse : HashMap<String, Any>()

object ErrorExceptionMapping {
    fun register(app: Javalin) {
        app.exception(Exception::class.java) { e, ctx ->
            val error = ErrorResponse()
            error["Unknow Error"] = listOf(e.message)
            ctx.json(error).status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        }
        app.exception(BadRequestResponse::class.java) { _, ctx ->
            val error = ErrorResponse()
            error["body"] = "can't be empty"
            ctx.json(error).status(HttpStatus.UNPROCESSABLE_ENTITY_422)
        }
        app.exception(UnauthorizedResponse::class.java) { e, ctx ->
            val error = ErrorResponse()
            error["login"] = e.message ?: "User not authorized!"
            ctx.json(error).status(HttpStatus.UNAUTHORIZED_401)
        }
        app.exception(HttpResponseException::class.java) { e, ctx ->
            val error = ErrorResponse()
            error["body"] = listOf(e.message)
            ctx.json(error).status(e.status)
        }
    }
}