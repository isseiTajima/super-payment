package jp.co.payment.domain

@JvmInline
value class Description(val value: String) {
    init {
        require(value.isNotBlank()) { "説明は必須です" }
    }
}