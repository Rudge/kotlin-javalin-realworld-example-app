package io.realworld.app.domain.repository

import io.realworld.app.domain.Article
import io.realworld.app.domain.User
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import javax.sql.DataSource

private object Articles : Table() {
    val slug: Column<String> = varchar("slug", 100).primaryKey()
    val title: Column<String> = varchar("title", 150)
    val description: Column<String> = varchar("description", 150)
    val body: Column<String> = varchar("body", 1000)
    val createdAt: Column<DateTime> = date("created_at")
    val updatedAt: Column<DateTime> = date("updated_at")
    val author: Column<Long> = long("author")

    fun toDomain(row: ResultRow, author: User?): Article {
        return Article(
                slug = row[slug],
                title = row[title],
                description = row[description],
                body = row[body],
                createdAt = row[createdAt].toDate(),
                updatedAt = row[updatedAt].toDate(),
                author = author
        )
    }
}

class ArticleRepository(private val dataSource: DataSource) {

    init {
        transaction(Database.connect(dataSource)) {
            SchemaUtils.create(Articles)
        }
    }

    private fun findWithConditional(where: Op<Boolean>, limit: Int, offset: Int): List<Article> {
        var articles = emptyList<Article>()
        transaction(Database.connect(dataSource)) {
            articles = Articles.join(Users, JoinType.INNER,
                    additionalConstraint = { Articles.author eq Users.id })
                    .select { where }
                    .limit(limit, offset)
                    .orderBy(Articles.createdAt, true)
                    .map { row ->
                        Articles.toDomain(row, Users.toDomain(row))
                    }
        }
        return articles
    }

    fun findByTag(tag: String, limit: Int, offset: Int): List<Article> {
        return emptyList()
    }

    fun findByFavorited(favorited: String, limit: Int, offset: Int): List<Article> {
        return findWithConditional((Articles.slug eq favorited), limit, offset)
    }

    fun create(article: Article): Article? {
        transaction(Database.connect(dataSource)) {
            Articles.insert { row ->
                row[slug] = article.slug!!
                row[title] = article.title
                row[description] = article.description
                row[body] = article.body
                row[createdAt] = DateTime()
                row[updatedAt] = DateTime()
                row[author] = article.author?.id!!
            }
        }
        return findBySlug(article.slug!!)
    }

    fun findAll(limit: Int, offset: Int): List<Article> {
        var articles = emptyList<Article>()
        transaction(Database.connect(dataSource)) {
            val query = Articles.join(Users, JoinType.INNER,
                    additionalConstraint = { Articles.author eq Users.id })
                    .selectAll()
                    .limit(limit, offset)
                    .orderBy(Articles.createdAt, true)
                    .map { row ->
                        Articles.toDomain(row, Users.toDomain(row))
                    }
        }
        return articles
    }

    fun findBySlug(slug: String): Article? {
        return findWithConditional((Articles.slug eq slug), 1, 0).firstOrNull()
    }

    fun findByAuthor(author: String, limit: Int, offset: Int): List<Article> {
        return findWithConditional((Users.username eq author), limit, offset)
    }

    fun update(slug: String, article: Article): Article? {
        transaction(Database.connect(dataSource)) {
            Articles.update({ Articles.slug eq slug }) { row ->
                row[Articles.slug] = article.slug!!
                row[title] = article.title
                row[description] = article.description
                row[body] = article.body
                row[updatedAt] = DateTime()
                if (article.author != null)
                    row[author] = article.author.id!!
            }
        }
        return findBySlug(article.slug!!)
    }

    fun favorite(slug: String): Article {
        transaction(Database.connect(dataSource)) {

        }
        return Article("", "", "", "", favorited = true, favoritesCount = 1)
    }

    fun unfavorite(slug: String): Article {
        transaction(Database.connect(dataSource)) {

        }
        return Article("", "", "", "")
    }

    fun delete(slug: String): Article {
        transaction(Database.connect(dataSource)) {

        }
        return Article("", "", "", "")
    }
}