package jp.co.payment.domain

import java.math.BigDecimal

@JvmInline
value class Tax(val value: BigDecimal) : Comparable<Tax> {
    override fun compareTo(other: Tax): Int {
        return value.compareTo(other.value)
    }
}
