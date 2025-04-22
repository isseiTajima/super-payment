package jp.co.payment.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TaxAmountTest {

    @Test
    @DisplayName("税額が正しく計算されることを確認する")
    fun calculate_taxAmountCalculation() {
        // Given
        val fee = Fee.calculate(PaymentAmount(BigDecimal("10000")), FeeRate(BigDecimal("0.05")))
        val taxRate = TaxRate(BigDecimal("1.10")) // 10% tax

        // When
        val taxAmount = TaxAmount.calculate(fee, taxRate)

        // Then
        // fee = 500.00, tax = 500 * 0.10 = 50.00
        assertThat(taxAmount.value).isEqualTo(BigDecimal("50.00"))
    }

    @Test
    @DisplayName("小数点以下の端数が正しく処理されることを確認する")
    fun calculate_decimalPointHandling() {
        // Given
        val fee = Fee.calculate(PaymentAmount(BigDecimal("10001")), FeeRate(BigDecimal("0.05")))
        val taxRate = TaxRate(BigDecimal("1.08")) // 8% tax

        // When
        val taxAmount = TaxAmount.calculate(fee, taxRate)

        // Then
        // fee = 500.05, tax = 500.05 * 0.08 = 40.004 -> 40.00 (rounded)
        assertThat(taxAmount.value).isEqualTo(BigDecimal("40.00"))
    }

    @Test
    @DisplayName("異なる税額の比較が正しく行われることを確認する")
    fun compareTo_differentTaxAmountComparison() {
        // Given
        val fee1 = Fee.calculate(PaymentAmount(BigDecimal("10000")), FeeRate(BigDecimal("0.05")))
        val fee2 = Fee.calculate(PaymentAmount(BigDecimal("20000")), FeeRate(BigDecimal("0.05")))
        val taxRate = TaxRate(BigDecimal("1.10"))

        val taxAmount1 = TaxAmount.calculate(fee1, taxRate)
        val taxAmount2 = TaxAmount.calculate(fee2, taxRate)
        val taxAmount3 = TaxAmount.calculate(fee1, taxRate)

        // Then
        assertThat(taxAmount1).isLessThan(taxAmount2)
        assertThat(taxAmount2).isGreaterThan(taxAmount1)
        assertThat(taxAmount1).isEqualTo(taxAmount3)
    }
}
