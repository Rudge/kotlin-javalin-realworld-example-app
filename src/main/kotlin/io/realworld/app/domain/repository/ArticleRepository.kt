package io.realworld.app.domain.repository

import io.javalin.NotFoundResponse
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
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import javax.sql.DataSource

internal object Articles : Table() {
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

internal object Favorites : Table() {
    val slug: Column<String> = varchar("slug", 100).primaryKey()
    val user: Column<Long> = long("user").primaryKey()
}

class ArticleRepository(private val dataSource: DataSource) {

    init {
        transaction(Database.connect(dataSource)) {
            SchemaUtils.create(Articles)
            SchemaUtils.create(Favorites)
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
                        val favoritesCount = Favorites.select { Favorites.slug eq row[Articles.slug] }.count()
                        Articles.toDomain(row, Users.toDomain(row)).copy(favoritesCount = favoritesCount.toLong())
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
                row[title] = article.title!!
                row[description] = article.description!!
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
            Articles.join(Users, JoinType.INNER,
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
        var favoritesCount = 0
        transaction(Database.connect(dataSource)) {
            Articles.update({ Articles.slug eq slug }) { row ->
                if (article.slug != null)
                    row[Articles.slug] = article.slug
                if (article.title != null)
                    row[title] = article.title
                if (article.description != null)
                    row[description] = article.description
                row[body] = article.body
                row[updatedAt] = DateTime()
                if (article.author != null)
                    row[author] = article.author.id!!
            }
            if (article.slug != null) {
                Favorites.update({ Favorites.slug eq slug }) { row ->
                    row[Favorites.slug] = slug
                }
            }
            favoritesCount = Favorites.select { Favorites.slug eq article.slug!! }.count()
        }
        return findBySlug(article.slug!!)?.copy(favoritesCount = favoritesCount.toLong())
    }

    fun favorite(userId: Long, slug: String): Article? {
        var favoritesCount = 0
        val article = findBySlug(slug) ?: throw NotFoundResponse()
        transaction(Database.connect(dataSource)) {
            Favorites.insert { row ->
                row[Favorites.slug] = article.slug!!
                row[Favorites.user] = userId
            }.also {
                favoritesCount = Favorites.select { Favorites.slug eq article.slug!! }.count()
            }
        }
        return article.copy(favorited = true, favoritesCount = favoritesCount.toLong())
    }

    fun unfavorite(userId: Long, slug: String): Article? {
        var favoritesCount = 0
        val article = findBySlug(slug) ?: throw NotFoundResponse()
        transaction(Database.connect(dataSource)) {
            Favorites.deleteWhere {
                Favorites.slug eq article.slug!! and (Favorites.user eq userId)
            }.also {
                favoritesCount = Favorites.select { Favorites.slug eq article.slug!! }.count()
            }
        }
        return article.copy(favoritesCount = favoritesCount.toLong())
    }

    fun delete(slug: String) {
        transaction(Database.connect(dataSource)) {
            Articles.deleteWhere { Articles.slug eq slug }
            Favorites.deleteWhere { Favorites.slug eq slug }
        }
    }
}