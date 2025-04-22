package jp.co.payment.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class InvoiceTest {

    @Test
    @DisplayName("請求書が正しく作成されることを確認する")
    fun create_invoiceCreation() {
        // Given
        val userId = 2
        val paymentAmount = PaymentAmount(BigDecimal("20000"))
        val issueDate = IssueDate(LocalDate.of(2023, 1, 15))
        val dueDate = DueDate(LocalDate.now().plusDays(30))
        val feeRate = FeeRate(BigDecimal("0.05"))
        val taxRate = TaxRate(BigDecimal("1.08"))

        // When
        val invoice = Invoice.create(
            userId = userId,
            paymentAmount = paymentAmount,
            issueDate = issueDate,
            dueDate = dueDate,
            feeRate = feeRate,
            taxRate = taxRate
        )

        // Then
        assertThat(invoice.userId).isEqualTo(userId)
        assertThat(invoice.paymentAmount).isEqualTo(paymentAmount)
        assertThat(invoice.issueDate).isEqualTo(issueDate)
        assertThat(invoice.dueDate).isEqualTo(dueDate)
        assertThat(invoice.feeRate).isEqualTo(feeRate)
        assertThat(invoice.taxRate).isEqualTo(taxRate)

        // 計算結果の確認
        // (20000 * 0.05 = 1000)
        assertThat(invoice.fee.value).isEqualTo(BigDecimal("1000.00"))

        // (1000 * 0.08 = 80)
        assertThat(invoice.taxAmount.value).isEqualTo(BigDecimal("80.00"))

        // (20000 + 1000 + 80 = 21080)
        assertThat(invoice.totalAmount.value).isEqualTo(BigDecimal("21080.00"))
    }
}
