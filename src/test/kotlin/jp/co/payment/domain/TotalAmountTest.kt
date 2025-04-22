package jp.co.payment.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TotalAmountTest {

    @Test
    @DisplayName("合計金額が正しく計算されることを確認する")
    fun of_totalAmountCalculation() {
        // Given
        val paymentAmount = PaymentAmount(BigDecimal("10000"))
        val feeRate = FeeRate(BigDecimal("0.05"))
        val fee = Fee.calculate(paymentAmount, feeRate)
        val taxRate = TaxRate(BigDecimal("1.10"))
        val taxAmount = TaxAmount.calculate(fee, taxRate)

        // When
        val totalAmount = TotalAmount.of(paymentAmount, fee, taxAmount)

        // Then
        // payment = 10000, fee = 500.00, tax = 50.00
        // total = 10000 + 500.00 + 50.00 = 10550.00
        assertThat(totalAmount.value).isEqualTo(BigDecimal("10550.00"))
    }

    @Test
    @DisplayName("小数点以下の端数が正しく処理されることを確認する")
    fun of_decimalPointHandling() {
        // Given
        val paymentAmount = PaymentAmount(BigDecimal("10001.33"))
        val feeRate = FeeRate(BigDecimal("0.05"))
        val fee = Fee.calculate(paymentAmount, feeRate)
        val taxRate = TaxRate(BigDecimal("1.08"))
        val taxAmount = TaxAmount.calculate(fee, taxRate)

        // When
        val totalAmount = TotalAmount.of(paymentAmount, fee, taxAmount)

        // Then
        // payment = 10001.33, fee = 500.07, tax = 40.01
        // total = 10001.33 + 500.07 + 40.01 = 10541.41
        assertThat(totalAmount.value).isEqualTo(BigDecimal("10541.41"))
    }

    @Test
    @DisplayName("異なる合計金額の比較が正しく行われることを確認する")
    fun compareTo_differentTotalAmountComparison() {
        // Given
        val paymentAmount1 = PaymentAmount(BigDecimal("10000"))
        val paymentAmount2 = PaymentAmount(BigDecimal("20000"))
        val feeRate = FeeRate(BigDecimal("0.05"))
        val taxRate = TaxRate(BigDecimal("1.10"))

        val fee1 = Fee.calculate(paymentAmount1, feeRate)
        val fee2 = Fee.calculate(paymentAmount2, feeRate)
        val taxAmount1 = TaxAmount.calculate(fee1, taxRate)
        val taxAmount2 = TaxAmount.calculate(fee2, taxRate)

        val totalAmount1 = TotalAmount.of(paymentAmount1, fee1, taxAmount1)
        val totalAmount2 = TotalAmount.of(paymentAmount2, fee2, taxAmount2)
        val totalAmount3 = TotalAmount.of(paymentAmount1, fee1, taxAmount1)

        // Then
        assertThat(totalAmount1).isLessThan(totalAmount2)
        assertThat(totalAmount2).isGreaterThan(totalAmount1)
        assertThat(totalAmount1).isEqualTo(totalAmount3)
    }
}
