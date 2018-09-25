package io.realworld.app.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.json.JavalinJackson

class MapperConfig {

    companion object {
        fun configure() {
            JavalinJackson.configure(
                    jacksonObjectMapper()
                            .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
                            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true)
                            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
            )
        }
    }
}