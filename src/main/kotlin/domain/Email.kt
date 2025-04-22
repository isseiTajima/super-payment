package jp.co.payment.domain

import org.apache.commons.validator.routines.EmailValidator

@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "メールアドレスは必須です" }
        require(value.length <= 255) { "メールアドレスは255文字以内で入力してください" }
        require(
            EmailValidator.getInstance().isValid(value)
        ) { "無効なメールアドレス形式です" }
    }
}