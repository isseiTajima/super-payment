package jp.co.payment.domain

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class TotalAmount(val value: BigDecimal) : Comparable<TotalAmount> {
    override fun compareTo(other: TotalAmount): Int {
        return value.compareTo(other.value)
    }

    companion object {
        fun of(
            paymentAmount: PaymentAmount,
            fee: Fee,
            tax: TaxAmount
        ): TotalAmount = TotalAmount(
            paymentAmount.value
                .add(fee.value)
                .add(tax.value)
                .setScale(2, RoundingMode.HALF_UP)
        )
    }
}
