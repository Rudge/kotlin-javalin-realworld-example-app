package io.realworld.app.exception

import org.eclipse.jetty.http.HttpStatus

class InvalidRequestBodyException(private val errorMessage: String = "can't be empty") :
        HttpResponseException(field = "body", msg = errorMessage, status = HttpStatus.UNPROCESSABLE_ENTITY_422)