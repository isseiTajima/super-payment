package jp.co.payment.domain

interface UserRepository {
    fun save(user: User): User
    fun findByUsername(username: String): User?
}
