package jp.co.payment.domain

import java.time.LocalDate

@JvmInline
value class IssueDate(val value: LocalDate) {
    init {
        require(!value.isAfter(LocalDate.now().plusDays(1))) {
            "発行日は未来日を指定できません"
        }
    }
}