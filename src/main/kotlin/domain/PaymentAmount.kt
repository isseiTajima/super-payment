package jp.co.payment.domain

import java.math.BigDecimal

@JvmInline
value class PaymentAmount(val value: BigDecimal) : Comparable<PaymentAmount> {
    init {
        require(value >= BigDecimal.ZERO) { "支払金額は0以上である必要があります" }
    }

    override fun compareTo(other: PaymentAmount): Int {
        return value.compareTo(other.value)
    }

    override fun toString(): String {
        return if (value.stripTrailingZeros().scale() <= 0) {
            value.toBigInteger().toString()
        } else {
            value.toString()
        }
    }

    companion object {
        fun fromString(value: String): PaymentAmount {
            return PaymentAmount(BigDecimal(value))
        }
    }
}
