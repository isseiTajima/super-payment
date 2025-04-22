package jp.co.payment.domain

import java.time.LocalDate

data class Invoice(
    val invoiceId: InvoiceId?,
    val userId: UserId,
    val issueDate: IssueDate,
    val paymentAmount: PaymentAmount,
    val fee: Fee,
    val feeRate: FeeRate,
    val taxAmount: TaxAmount,
    val taxRate: TaxRate,
    val totalAmount: TotalAmount,
    val dueDate: DueDate,
) {
    companion object {
        fun create(
            userId: UserId,
            paymentAmount: PaymentAmount,
            issueDate: IssueDate = IssueDate(LocalDate.now()),
            dueDate: DueDate,
            feeRate: FeeRate,
            taxRate: TaxRate
        ): Invoice {
            val fee = Fee.calculate(paymentAmount, feeRate)
            val tax = TaxAmount.calculate(fee, taxRate)
            return Invoice(
                invoiceId = null,
                userId = userId,
                issueDate = issueDate,
                paymentAmount = paymentAmount,
                fee = fee,
                feeRate = feeRate,
                taxAmount = tax,
                taxRate = taxRate,
                totalAmount = TotalAmount.of(paymentAmount, fee, tax),
                dueDate = dueDate,
            )
        }
    }
}

typealias InvoiceId = Int
