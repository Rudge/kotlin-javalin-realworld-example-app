package io.realworld.app.exception

open class HttpResponseException(val status: Int, val field: String, val msg: String) : RuntimeException()