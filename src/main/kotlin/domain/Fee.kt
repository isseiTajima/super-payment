package jp.co.payment.domain

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Fee private constructor(val value: BigDecimal) : Comparable<Fee> {
    override fun compareTo(other: Fee): Int {
        return value.compareTo(other.value)
    }

    companion object {
        fun calculate(
            paymentAmount: PaymentAmount,
            feeRate: FeeRate
        ): Fee = Fee(
            paymentAmount.value.multiply(feeRate.value)
                .setScale(2, RoundingMode.HALF_UP)
        )
    }
}
