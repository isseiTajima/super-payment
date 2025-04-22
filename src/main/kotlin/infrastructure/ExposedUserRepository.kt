package jp.co.payment.infrastructure

import jp.co.payment.domain.User
import jp.co.payment.domain.UserRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Exposed を使用したUserRepositoryの実装。
 * ユーザーエンティティのデータベース操作を提供します。
 */
class ExposedUserRepository : UserRepository {
    /**
     * ユーザーを永続化します。
     */
    override fun save(user: User): User {
        return transaction {
            Users.insert {
                it[companyName] = user.companyName.value
                it[name] = user.userName.value
                it[email] = user.email.value
                it[password] = user.password.value
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }.let {
                Users.toDomain(it)
            }
        }
    }

    /**
     * ユーザー名でユーザーを検索し、ユーザーを返します。
     */
    override fun findByUsername(username: String): User? {
        return transaction {
            Users.select {
                (Users.name eq username)
            }.firstOrNull()
                ?.let {
                    Users.toDomain(it)
                }
        }
    }
}
