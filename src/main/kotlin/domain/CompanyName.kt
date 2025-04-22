package jp.co.payment.domain

@JvmInline
value class CompanyName(val value: String) {
    init {
        require(value.isNotBlank()) { "企業名は必須です" }
        require(value.length <= 255) { "企業名は255文字以内で入力してください" }
    }
}