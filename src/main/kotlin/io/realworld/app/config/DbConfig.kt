package io.realworld.app.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.h2.tools.Server
import javax.sql.DataSource

class DbConfig(jdbcUrl: String, username: String, password: String) {
    private val dataSource: DataSource

    init {
        Server.createPgServer().start()
        dataSource = HikariConfig().let { config ->
            config.jdbcUrl = jdbcUrl
            config.username = username
            config.password = password
            HikariDataSource(config)
        }
    }

    fun getDataSource(): DataSource {
        return dataSource
    }
}