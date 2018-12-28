package io.realworld.app.domain.repository

import io.realworld.app.domain.User
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import javax.sql.DataSource

object Users : LongIdTable() {
    val email: Column<String> = varchar("email", 200).uniqueIndex()
    val username: Column<String?> = varchar("username", 100).nullable()
    val password: Column<String> = varchar("password", 150)
    val bio: Column<String?> = varchar("bio", 1000).nullable()
    val image: Column<String?> = varchar("image", 255).nullable()

    fun toDomain(row: ResultRow): User {
        return User(
                id = row[Users.id].value,
                email = row[Users.email],
                username = row[Users.username],
                password = row[Users.password],
                bio = row[Users.bio],
                image = row[Users.image]
        )
    }
}

class UserRepository(private val dataSource: DataSource) {
    init {
        transaction(Database.connect(dataSource)) {
            SchemaUtils.create(Users)
        }
    }

    fun findById(id: Long): User? {
        var user: User? = null
        transaction(Database.connect(dataSource)) {
            val query = Users.select { Users.id eq id }
            user = query.map { Users.toDomain(it) }.firstOrNull()
        }
        return user
    }

    fun findByEmail(email: String): User? {
        var user: User? = null
        transaction(Database.connect(dataSource)) {
            val query = Users.select { Users.email eq email }
            user = query.map { Users.toDomain(it) }.firstOrNull()
        }
        return user
    }

    fun create(user: User): Long? {
        var id: Long? = null
        transaction(Database.connect(dataSource)) {
            id = Users.insertAndGetId { row ->
                row[Users.email] = user.email
                row[Users.username] = user.username
                row[Users.password] = user.password
                row[Users.bio] = user.bio
                row[Users.image] = user.image
            }.value
        }
        return id
    }

    fun update(user: User): User? {
        transaction(Database.connect(dataSource)) {
            Users.update({ Users.id eq user.id }) { row ->
                row[Users.email] = user.email
                row[Users.username] = user.username
                row[Users.password] = user.password
                row[Users.bio] = user.bio
                row[Users.image] = user.image
            }
        }
        return findById(user.id!!)
    }
}