package jp.co.payment.domain

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class TaxAmount private constructor(val value: BigDecimal) : Comparable<TaxAmount> {
    override fun compareTo(other: TaxAmount): Int {
        return value.compareTo(other.value)
    }

    companion object {
        fun calculate(
            fee: Fee,
            taxRate: TaxRate
        ): TaxAmount = TaxAmount(
            fee.value.multiply(taxRate.value.subtract(BigDecimal.ONE))
                .setScale(2, RoundingMode.HALF_UP)
        )
    }
}
