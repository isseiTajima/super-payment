package jp.co.payment.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class FeeTest {

    @Test
    @DisplayName("手数料が正しく計算されることを確認する")
    fun calculate_feeCalculation() {
        // Given
        val paymentAmount = PaymentAmount(BigDecimal("10000"))
        val feeRate = FeeRate(BigDecimal("0.05"))

        // When
        val fee = Fee.calculate(paymentAmount, feeRate)

        // Then
        // 10000 * 0.05 = 500
        assertThat(fee.value).isEqualTo(BigDecimal("500.00"))
    }

    @Test
    @DisplayName("小数点以下の端数が正しく処理されることを確認する")
    fun calculate_decimalPointHandling() {
        // Given
        val paymentAmount = PaymentAmount(BigDecimal("10001"))
        val feeRate = FeeRate(BigDecimal("0.05"))

        // When
        val fee = Fee.calculate(paymentAmount, feeRate)

        // Then
        // 10001 * 0.05 = 500.05
        assertThat(fee.value).isEqualTo(BigDecimal("500.05"))
    }

    @Test
    @DisplayName("異なる手数料の比較が正しく行われることを確認する")
    fun compareTo_differentFeeComparison() {
        // Given
        val fee1 = Fee.calculate(PaymentAmount(BigDecimal("10000")), FeeRate(BigDecimal("0.05")))
        val fee2 = Fee.calculate(PaymentAmount(BigDecimal("20000")), FeeRate(BigDecimal("0.05")))
        val fee3 = Fee.calculate(PaymentAmount(BigDecimal("10000")), FeeRate(BigDecimal("0.05")))

        // Then
        assertThat(fee1).isLessThan(fee2)
        assertThat(fee2).isGreaterThan(fee1)
        assertThat(fee1).isEqualTo(fee3)
    }
}
