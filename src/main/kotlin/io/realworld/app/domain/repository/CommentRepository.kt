package io.realworld.app.domain.repository

import io.javalin.BadRequestResponse
import io.javalin.NotFoundResponse
import io.realworld.app.domain.Comment
import io.realworld.app.domain.User
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import javax.sql.DataSource

private object Comments : LongIdTable() {
    val body: Column<String> = varchar("body", 1000)
    val createdAt: Column<DateTime> = date("created_at")
    val updatedAt: Column<DateTime> = date("updated_at")
    val slug: Column<String> = varchar("slug", 100)
    val author: Column<Long> = long("author")

    fun toDomain(row: ResultRow, author: User?): Comment {
        return Comment(id = row[Comments.id].value,
                body = row[Comments.body],
                createdAt = row[Comments.createdAt].toDate(),
                updatedAt = row[Comments.updatedAt].toDate(),
                author = author)
    }
}

class CommentRepository(private val dataSource: DataSource) {
    init {
        transaction(Database.connect(dataSource)) {
            SchemaUtils.create(Comments)
        }
    }

    private fun findById(commentId: Long): Comment? {
        var comment: Comment? = null
        transaction(Database.connect(dataSource)) {
            comment = Comments.select { Comments.id eq commentId }.map { Comments.toDomain(it, null) }.firstOrNull()
        }
        return comment
    }

    fun add(slugCommented: String, email: String, comment: Comment): Comment? {
        var commentId = 0L
        var user: User? = null
        transaction(Database.connect(dataSource)) {
            user = Users.select { Users.email eq email }
                    .map { Users.toDomain(it) }.firstOrNull() ?: throw BadRequestResponse()
            commentId = Comments.insertAndGetId { row ->
                row[body] = comment.body
                row[createdAt] = DateTime()
                row[updatedAt] = DateTime()
                row[slug] = slugCommented
                row[author] = user?.id!!
            }.value
        }
        return findById(commentId)?.copy(author = user)
    }

    fun findBySlug(slug: String): List<Comment> {
        val comments = mutableListOf<Comment>()
        transaction(Database.connect(dataSource)) {
            comments.addAll(
                    Comments.join(Users, JoinType.INNER, additionalConstraint = { Comments.author eq Users.id })
                            .select { Comments.slug eq slug }
                            .map { Comments.toDomain(it, Users.toDomain(it)) }
            )
        }
        return comments
    }

    fun delete(id: Long, slug: String) {
        transaction(Database.connect(dataSource)) {
            Comments.deleteWhere { Comments.id eq id and (Comments.slug eq slug) }
                    .takeIf { it == 0 }
                    ?.apply { throw NotFoundResponse() }
        }
    }
}