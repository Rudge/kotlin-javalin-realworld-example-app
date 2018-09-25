package io.realworld.app.domain

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("errors")
class ErrorResponse : HashMap<String, List<String?>>()