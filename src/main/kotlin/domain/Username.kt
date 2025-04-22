package jp.co.payment.domain

@JvmInline
value class Username(val value: String) {
    init {
        require(value.isNotBlank()) { "氏名は必須です" }
        require(value.length <= 255) { "氏名は255文字以内で入力してください" }
    }
}