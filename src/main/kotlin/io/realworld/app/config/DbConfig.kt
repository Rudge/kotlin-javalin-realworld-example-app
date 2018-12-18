package io.realworld.app.config

import com.zaxxer.hikari.HikariConfig
import javax.sql.DataSource

class DbConfig(val jdbcUrl: String, val username: String, val password: String) {
    fun getDataSource(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = username
        config.password = password
        return config.dataSource
    }
}