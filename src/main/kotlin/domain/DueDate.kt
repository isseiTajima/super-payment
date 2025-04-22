package jp.co.payment.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@JvmInline
value class DueDate(val value: LocalDate) {
    init {
        require(value.isAfter(LocalDate.now().minusDays(1))) { "支払期日は今日以降である必要があります" }
    }

    companion object {
        fun fromString(value: String): DueDate {
            try {
                val date = LocalDate.parse(value, DateTimeFormatter.ISO_DATE)
                return DueDate(date)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("無効な日付形式です。YYYY-MM-DD形式で入力してください。", e)
            }
        }
    }
}