package jp.co.payment.application

import jp.co.payment.domain.User
import jp.co.payment.domain.UserRepository

/**
 * 新しいユーザーを作成するユースケース。
 */
class CreateUserUseCase(private val userRepository: UserRepository) {

    /**
     * 新しいユーザーを作成します。
     */
    fun execute(
        user: User,
    ): User {
        return userRepository.save(user)
    }
}
