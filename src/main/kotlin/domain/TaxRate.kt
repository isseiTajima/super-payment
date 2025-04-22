package jp.co.payment.domain

import java.math.BigDecimal

@JvmInline
value class TaxRate(val value: BigDecimal) : Comparable<TaxRate> {
    init {
        require(value >= BigDecimal.ONE) { "消費税率は1以上でなければなりません" }
    }

    override fun compareTo(other: TaxRate): Int {
        return value.compareTo(other.value)
    }
}
