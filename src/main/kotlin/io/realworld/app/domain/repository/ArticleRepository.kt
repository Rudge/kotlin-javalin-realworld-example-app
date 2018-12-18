package io.realworld.app.domain.repository

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import javax.sql.DataSource

object Article : IntIdTable() {
    val slug: Column<Int> = integer("slug").uniqueIndex()
    val name: Column<String> = varchar("name", 50)
    val director: Column<String> = varchar("director", 50)
}

class ArticleRepository(val dataSource: DataSource) {

    fun findByTag() {

    }

    fun findByAuthor() {

    }

    fun findByFavorited() {

    }

}