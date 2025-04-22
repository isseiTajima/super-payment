package jp.co.payment.application

import jp.co.payment.domain.Password
import jp.co.payment.domain.UserRepository
import jp.co.payment.infrastructure.JwtService
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * ユーザー認証を担当するユースケース。
 * ユーザー認証情報を検証し、認証トークンを生成します。
 */
class AuthUseCase(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    /**
     * 提供された認証情報でユーザーを認証し、JWTトークンを生成します。
     */
    fun execute(
        username: String,
        password: String,
    ): String {
        val user = transaction {
            userRepository.findByUsername(username)?.let {
                // パスワードを検証し、問題なければユーザーを返す
                if (Password.verify(password, it.password)) {
                    it
                } else {
                    null
                }
            }
        } ?: throw IllegalArgumentException("ユーザー名またはパスワードが無効です")

        return jwtService.generateToken(user)
    }
}
