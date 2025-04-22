package jp.co.payment.domain

import org.mindrot.jbcrypt.BCrypt

@JvmInline
value class Password(val value: String) {
    init {
        require(value.isNotBlank()) { "パスワードは必須です" }
    }

    companion object {
        fun hash(plainPassword: String): Password {
            require(plainPassword.isNotBlank()) { "パスワードは必須です" }
            require(plainPassword.length >= 8) { "パスワードは8文字以上で入力してください" }
            require(plainPassword.length <= 255) { "パスワードは255文字以内で入力してください" }

            val hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt())
            return Password(hashedPassword)
        }

        fun verify(plainPassword: String, hashedPassword: Password): Boolean {
            return try {
                BCrypt.checkpw(plainPassword, hashedPassword.value)
            } catch (_: Exception) {
                false
            }
        }
    }
}
