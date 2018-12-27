package io.realworld.app.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

class DbConfig(jdbcUrl: String, username: String, password: String) {
    private val dataSource: DataSource

    init {
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = username
        config.password = password
        dataSource = HikariDataSource(config)
    }

    fun getDataSource(): DataSource {
        return dataSource
    }
}