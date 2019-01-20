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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
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

internal object Favorites : Table() {
    val slug: Column<String> = varchar("slug", 100).primaryKey()
    val user: Column<Long> = long("user").primaryKey()
}

internal object ArticlesTags : Table() {
    val tag: Column<Long> = long("tag").primaryKey()
    val slug: Column<String> = varchar("slug", 100).primaryKey()
}

class ArticleRepository(private val dataSource: DataSource) {

    init {
        transaction(Database.connect(dataSource)) {
            SchemaUtils.create(Articles)
            SchemaUtils.create(Favorites)
            SchemaUtils.create(ArticlesTags)
        }
    }

    private fun findWithConditional(where: Op<Boolean>, limit: Int, offset: Int): List<Article> {
        var articles = emptyList<Article>()

        transaction(Database.connect(dataSource)) {
            articles = Articles.join(Users, JoinType.INNER, additionalConstraint = { Articles.author eq Users.id })
                    .select { where }
                    .limit(limit, offset)
                    .orderBy(Articles.createdAt, true)
                    .map { row ->
                        val favoritesCount = Favorites.select { Favorites.slug eq row[Articles.slug] }.count()
                        Articles.toDomain(row, Users.toDomain(row))
                                .copy(favorited = favoritesCount > 0,
                                        favoritesCount = favoritesCount.toLong(),
                                        tagList = Tags.join(ArticlesTags, JoinType.INNER,
                                                additionalConstraint = { Tags.id eq ArticlesTags.tag })
                                                .select { ArticlesTags.slug eq row[Articles.slug] }
                                                .map { it[Tags.name] })
                    }
        }
        return articles
    }

    fun findByTag(tag: String, limit: Int, offset: Int): List<Article> {
        return transaction(Database.connect(dataSource)) {
            Tags.join(ArticlesTags, JoinType.INNER, additionalConstraint = { Tags.id eq ArticlesTags.tag })
                    .select { Tags.name eq tag }
                    .map { it[ArticlesTags.slug] }
                    .let {
                        findWithConditional((Articles.slug inList it), limit, offset)
                    }
        }
    }

    fun findByFavorited(favorited: String, limit: Int, offset: Int): List<Article> {
        var articlesFavorited = emptyList<String>()
        transaction(Database.connect(dataSource)) {
            articlesFavorited = Favorites.join(Users, JoinType.INNER, additionalConstraint = { Favorites.user eq Users.id })
                    .slice(Favorites.slug)
                    .select { Users.username eq favorited }
                    .map { it[Favorites.slug] }
        }
        return findWithConditional((Articles.slug inList articlesFavorited), limit, offset)
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
            article.tagList.map { tag ->
                Tags.slice(Tags.id).select { Tags.name eq tag }.map { row -> row[Tags.id].value }.firstOrNull()
                        ?: Tags.insertAndGetId { it[name] = tag }.value

            }.also {
                ArticlesTags.batchInsert(it) { tagId ->
                    this[ArticlesTags.tag] = tagId
                    this[ArticlesTags.slug] = article.slug!!
                }
            }
        }
        return findBySlug(article.slug!!)
    }

    fun findAll(limit: Int, offset: Int): List<Article> {
        var articles = emptyList<Article>()

        transaction(Database.connect(dataSource)) {
            articles = Articles.join(Users, JoinType.INNER, additionalConstraint = { Articles.author eq Users.id })
                    .selectAll()
                    .limit(limit, offset)
                    .orderBy(Articles.createdAt, true)
                    .map { row ->
                        val favoritesCount = Favorites.select { Favorites.slug eq row[Articles.slug] }.count()
                        Articles.toDomain(row, Users.toDomain(row))
                                .copy(favoritesCount = favoritesCount.toLong(), tagList =
                                Tags.join(ArticlesTags, JoinType.INNER,
                                        additionalConstraint = { Tags.id eq ArticlesTags.tag })
                                        .select { ArticlesTags.slug eq row[Articles.slug] }
                                        .map { it[Tags.name] })
                    }
        }
        return articles
    }

    fun findFeed(email: String, limit: Int, offset: Int): List<Article> {
        var authorsFollows = emptyList<Long>()
        transaction(Database.connect(dataSource)) {
            authorsFollows = Follows.join(Users, JoinType.INNER, additionalConstraint = { Follows.follower eq Users.id })
                    .slice(Follows.user)
                    .select { Users.email eq email }
                    .map { it[Follows.user] }
        }
        return findWithConditional((Articles.author inList authorsFollows), limit, offset)
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
        return article.copy(favorited = false, favoritesCount = favoritesCount.toLong())
    }

    fun delete(slug: String) {
        transaction(Database.connect(dataSource)) {
            Articles.deleteWhere { Articles.slug eq slug }
            Favorites.deleteWhere { Favorites.slug eq slug }
        }
    }
}