package jp.co.payment.domain

import java.math.BigDecimal

@JvmInline
value class FeeRate(val value: BigDecimal) : Comparable<FeeRate> {
    init {
        require(value in BigDecimal.ZERO..BigDecimal.ONE) { "手数料率は0〜1の範囲でなければなりません" }
    }

    override fun compareTo(other: FeeRate): Int {
        return value.compareTo(other.value)
    }
}
