package io.realworld.app.domain.repository

import io.realworld.app.domain.Article
import io.realworld.app.domain.User
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import javax.sql.DataSource

private object Articles : LongIdTable() {
    val slug: Column<String?> = varchar("slug", 100).nullable().uniqueIndex()
    val title: Column<String> = varchar("title", 150)
    val description: Column<String> = varchar("description", 150)
    val body: Column<String> = varchar("body", 1000)
    //val tagList: List<String> = listOf(),
    val createdAt: Column<DateTime> = date("created_at")
    val updatedAt: Column<DateTime> = date("updated_at")
    val favorited: Column<Boolean> = bool("favorited")
    val author: Column<Long> = long("author")

    fun toDomain(row: ResultRow): Article {
        return Article(
                slug = row[slug],
                title = row[title],
                description = row[description],
                body = row[body],
                createdAt = row[createdAt].toDate(),
                updatedAt = row[updatedAt].toDate(),
                favorited = row[favorited]
        )
    }
}

class ArticleRepository(private val dataSource: DataSource) {

    init {
        transaction(Database.connect(dataSource)) {
            SchemaUtils.create(Articles)
        }
    }

    fun findByTag(tag: String?, limit: Int, offset: Int): List<Article> {
        return emptyList()
    }

    fun findByAuthor(author: String?, limit: Int, offset: Int): List<Article> {
        return emptyList()
    }

    fun findByFavorited(favorited: String?, limit: Int, offset: Int): List<Article> {
        var articles = emptyList<Article>()
        transaction(Database.connect(dataSource)) {
            val query = Articles.select { Articles.favorited eq true }.limit(limit, offset)
            articles = query.map { row ->
                Articles.toDomain(row).let { article ->
                    article.copy(author = getAuthor())
                }
            }
        }
        return articles
    }

    private fun getAuthor(): User? {
        return Articles.join(Users, JoinType.INNER,
                additionalConstraint = {
                    Articles.author eq Users.id
                }
        ).selectAll().map { Users.toDomain(it) }.firstOrNull()
    }

    fun create(article: Article) {
        transaction(Database.connect(dataSource)) {
            Articles.insert { row ->
                row[slug] = article.slug
                row[title] = article.title
                row[description] = article.description
                row[body] = article.body
                row[createdAt] = DateTime(article.createdAt?.time)
                row[updatedAt] = DateTime(article.updatedAt?.time)
                row[favorited] = article.favorited
                row[author] = article.author?.id!!
            }
        }
    }

    fun findAll(limit: Int, offset: Int): List<Article> {
        var articles = emptyList<Article>()
        transaction(Database.connect(dataSource)) {
            val query = Articles.selectAll().limit(limit, offset)
            articles = query.map { row ->
                Articles.toDomain(row).let { article ->
                    article.copy(author = getAuthor())
                }
            }
        }
        return articles
    }
}